package org.javasim.examples;

import org.javasim.DTStat;
import org.javasim.Entity;
import org.javasim.EventNotice;
import org.javasim.FIFOQueue;
import org.javasim.JavaSim;
import org.javasim.Resource;
import org.javasim.Rng;

/**
 * Example illustrating use of VBASim for simulation of M/G/1 Queue.
 * 
 * See JavaSim package for generic declarations and for the supporting JavaSim
 * class
 */

public class MG1 {

  // parameters we may want to change
  private double  meanTBA;  // mean time between arrivals
  private double  meanST;   // mean service time
  private int     phases;   // number of phases in service distribution
  private double  runLength; // run length
  private double  warmUp;   // "warm-up" time

  // objects for simulation
  // these will usually be queues and statistics
  FIFOQueue       queue;    // customer queue
  DTStat          wait;     // discrete-time statistics on customer waiting
  Resource        server;   // server resource

  // simulation object and the random number generator
  private JavaSim javaSim;
  private Rng     generator;

  /**
   * Constructor
   */

  public MG1() {

    myInit();

  }

  /**
   * Run the M/G/1 simulation and output the results
   */

  private void runSimulation() {

    EventNotice nextEvent;

    for(int reps = 0; reps < 10; reps++) {
      javaSim.javaSimInit();
      javaSim.schedule("Arrival", generator.expon(meanTBA, 0));
      javaSim.schedule("EndSimulation", runLength);
      javaSim.schedule("ClearIt", warmUp);

      do {
        nextEvent = javaSim.calendarRemove();
        javaSim.setClock(nextEvent.getEventTime());
        if (nextEvent.getEventType() == "Arrival") {
          arrival();
        } else if (nextEvent.getEventType() == "EndOfService") {
          endOfService();
        } else if (nextEvent.getEventType() == "ClearIt") {
          javaSim.clearStats();
        }

      } while(nextEvent.getEventType() != "EndSimulation");

      // write output report for each replication
      javaSim.report(wait.mean(), reps + 1, 0);
      javaSim.report(queue.mean(javaSim.getClock()), reps + 1, 1);
      javaSim.report(queue.numQueue(), reps + 1, 2);
      javaSim.report(server.mean(javaSim.getClock()), reps + 1, 3);

    }
  }

  /**
   * Initialize the simulation
   */

  private void myInit() {

    // initialize the simulation
    String simulationName = "M/G/1 Simulation";
    javaSim = new JavaSim(simulationName);

    // initialize the simulation objects
    wait = new DTStat();
    queue = new FIFOQueue(javaSim);
    server = new Resource(javaSim);
    server.setUnits(1); // set the number of servers to 1

    // initialize the random number generator
    generator = new Rng();

    meanTBA = 1.0;
    meanST = 0.8;
    phases = 3;
    runLength = 55000.0;
    warmUp = 5000.0;

    // Add queues, resources and statistics that need to be
    // initialized between replications to the global collections

    javaSim.addDTStat(wait);
    javaSim.addQueue(queue);
    javaSim.addResource(server);

    // write headings for the output reports
    javaSim.report("Average Wait", 0, 0);
    javaSim.report("Average Number in Queue", 0, 1);
    javaSim.report("Number Remaining in Queue", 0, 2);
    javaSim.report("Server Utilization", 0, 3);

  }

  /**
   * Arrival event
   */

  private void arrival() {
    // schedule next arrival
    javaSim.schedule("Arrival", generator.expon(meanTBA, 0));

    // process the newly arriving customer

    queue.add(new Entity(javaSim.getClock()), javaSim.getClock());

    // If server is not busy, start service by seizing the server
    if (server.getBusy() == 0) {
      server.seize(1, javaSim.getClock());
      javaSim.schedule("EndOfService", generator.erlang(phases, meanST, 1));
    }

  }

  /**
   * End of service event
   */

  private void endOfService() {
    // remove departing customer from queue and record wait time

    Entity departingCustomer = (Entity) queue.remove(javaSim.getClock());
    wait.record(javaSim.getClock() - departingCustomer.getCreateTime());

    // Check to see if there is another customer; if yes start service
    // otherwise free the server

    if (queue.numQueue() > 0) {
      javaSim.schedule("EndOfService", generator.erlang(phases, meanST, 1));
    } else {
      server.free(1, javaSim.getClock());
    }
  }

  public static void main(String[] args) {
    MG1 mg1Simulation = new MG1();
    mg1Simulation.runSimulation();
  }

}
