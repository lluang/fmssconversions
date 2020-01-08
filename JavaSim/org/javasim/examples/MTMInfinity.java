package org.javasim.examples;

import org.javasim.CTStat;
import org.javasim.EventNotice;
import org.javasim.JavaSim;
import org.javasim.Rng;

/**
 * Example illustrating use of JavaSim for simulation of M(t)/M/infinity Queue
 * parking lot example. In this version parking time averages 2 hours; the
 * arrival rate varies around 100 per hour; the lot starts empty, and we look at
 * a 24-hour period.
 * 
 * See JavaSim package for generic declarations and for the supporting JavaSim
 * class
 */

public class MTMInfinity {
  // Parameters we may want to change
  private double  meanParkingTime;

  // simulation variables and statistics
  private int     n;              // Number in queue
  private CTStat  queueLength;    // use to keep statistics on n
  private int     maxQueue;       // largest observed value of n

  // simulation object and the random number generator
  private JavaSim javaSim;
  private Rng     generator;

  public MTMInfinity() {
    myInit();
  }

  /**
   * Run the MTMInfinity simulation and output the results
   */

  private void runSimulation() {

    EventNotice nextEvent;

    for(int reps = 0; reps < 1000; reps++) {
      n = 0;
      maxQueue = 0;
      javaSim.javaSimInit(); // initialize javaSim for each replication

      javaSim.schedule("Arrival", nspp(0));
      javaSim.schedule("EndSimulation", 24);

      do {
        nextEvent = javaSim.calendarRemove();
        javaSim.setClock(nextEvent.getEventTime());
        if (nextEvent.getEventType() == "Arrival") {
          arrival();
        } else if (nextEvent.getEventType() == "Departure") {
          departure();
        }
      } while(nextEvent.getEventType() != "EndSimulation");

      javaSim.report(queueLength.mean(javaSim.getClock()), reps + 1, 0);
      javaSim.report(maxQueue, reps + 1, 1);

    }

  }

  /**
   * Arrival event
   */

  private void arrival() {
    // schedule next arrival
    javaSim.schedule("Arrival", nspp(0));

    // update the number in queue and max
    n++;
    queueLength.record(n, javaSim.getClock());
    if (n > maxQueue) {
      maxQueue = n;
    }

    javaSim.schedule("Departure", generator.expon(meanParkingTime, 1));
  }

  /**
   * End of service eevent
   */

  private void departure() {
    // udpate number in queue
    n--;
    queueLength.record(n, javaSim.getClock());
  }

  /**
   * Initialize the simulation
   */

  private void myInit() {
    // initialize the random number generator
    generator = new Rng();

    // initialize the simulation
    String simulationName = "M(t)/M/infinity";
    javaSim = new JavaSim(simulationName);

    // initialize the queue length CTStat
    queueLength = new CTStat();

    meanParkingTime = 2.0;
    javaSim.addCTStat(queueLength);

    // Write headings for the output reports
    javaSim.report("Average Number in Queue", 0, 0);
    javaSim.report("Maximum Number in Queue", 0, 1);

  }

  /**
   * This function implements thinning to generate interarrival times from the
   * nonstationary Poisson arrival process representing car arrivals. Time units
   * are minutes.
   * 
   * @param stream
   *          Seed for the random number generator
   * @return interarrival time from nonstationary Poisson arrival process
   */

  private double nspp(int stream) {
    double possibleArrival = javaSim.getClock() + generator.expon(1.0 / 110.0, stream);

    while(generator.uniform(0, 1, stream) >= (100 + 10 * Math.sin(3.141593 * possibleArrival / 12.0)) / 110.0) {
      possibleArrival += generator.expon(1.0 / 110.0, stream);
    }

    return possibleArrival - javaSim.getClock();

  }

  public static void main(String[] args) {
    MTMInfinity mtmInf = new MTMInfinity();
    mtmInf.runSimulation();
  }

}
