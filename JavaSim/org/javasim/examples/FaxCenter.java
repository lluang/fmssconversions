package org.javasim.examples;

import org.javasim.DTStat;
import org.javasim.Entity;
import org.javasim.EventNotice;
import org.javasim.FIFOQueue;
import org.javasim.JavaSim;
import org.javasim.Resource;
import org.javasim.Rng;

/**
 * FaxCenter Simulation
 * 
 */

public class FaxCenter {
  // parameters we may want to change
  private double    meanRegular;     // mean entry time regular faxes
  private double    varRegular;      // variance entry time regular faxes
  private double    meanSpecial;     // mean entry time special faxes
  private double    varSpecial;      // variance entry time special faxes
  private double    runLength;       // length of the working day
  private int       numAgents;       // number of regular agents
  private int       numSpecialists;  // number of special agents
  private int       numAgentsPM;     // number of regular agents after noon
  private int       numSpecialistsPM; // number of special agents after noon

  // global objects needed for simulation
  private FIFOQueue regularQ;        // queue for all faxes
  private FIFOQueue specialQ;        // queue for special faxes
  private DTStat    regularWait;     // discrete-time statistics on fax waiting
  private DTStat    specialWait;     // discrete-time statistics on special fax
  // waiting
  private DTStat    regular10;       // discrete-time statistics on < 10
  // minutes threshold
  private DTStat    special10;       // discrete-time statistics on < 10
  // minutes threshold
  private Resource  agents;          // entry agents resource
  private Resource  specialists;     // specialists resource
  private double[]  aRate;           // arrival rates
  private double    maxRate;         // maximum arrival rate
  private double    period;          // period for which arrival rate stays
  // constant
  private int       nPeriods;        // number of periods in a "day"

  // simulation object and the random number generator
  private JavaSim   javaSim;
  private Rng       generator;

  public FaxCenter() {
    myInit();
  }

  /**
   * Run FaxCenter simulation
   */

  private void runSimulation() {

    EventNotice nextEvent;

    for(int reps = 0; reps < 10; reps++) {
      javaSim.javaSimInit();
      agents.setUnits(numAgents);
      specialists.setUnits(numSpecialists);
      javaSim.schedule("Arrival", nsppFax(aRate, maxRate, nPeriods, period, 0));
      javaSim.schedule("ChangeStaff", 4.0 * 60.0);
      do {
        nextEvent = javaSim.calendarRemove();
        javaSim.setClock(nextEvent.getEventTime());
        if (nextEvent.getEventType() == "Arrival") {
          arrival();
        } else if (nextEvent.getEventType() == "EndOfEntry") {
          endOfEntry((Entity) nextEvent.getWhichObject());
        } else if (nextEvent.getEventType() == "EndOfEntrySpecial") {
          endOfEntrySpecial((Entity) nextEvent.getWhichObject());
        } else if (nextEvent.getEventType() == "ChangeStaff") {
          agents.setUnits(numAgentsPM);
          specialists.setUnits(numSpecialistsPM);
        }

      } while(javaSim.calendarN() > 0); // stop when event calendar
      // empty

      javaSim.report(regularWait.mean(), reps + 1, 0);
      javaSim.report(regularQ.mean(javaSim.getClock()), reps + 1, 1);
      javaSim.report(agents.mean(javaSim.getClock()), reps + 1, 2);
      javaSim.report(specialWait.mean(), reps + 1, 3);
      javaSim.report(specialQ.mean(javaSim.getClock()), reps + 1, 4);
      javaSim.report(specialists.mean(javaSim.getClock()), reps + 1, 5);
      javaSim.report(regular10.mean(), reps + 1, 6);
      javaSim.report(special10.mean(), reps + 1, 7);
      javaSim.report(javaSim.getClock(), reps + 1, 8);

    }

  }

  private void arrival() {
    // Schedule next fax arrival if < 4 PM
    if (javaSim.getClock() < runLength) {
      javaSim.schedule("Arrival", nsppFax(aRate, maxRate, nPeriods, period, 0));
    } else {
      return;
    }

    // Process the newly arriving Fax

    Entity fax = new Entity(javaSim.getClock());
    if (agents.getBusy() < agents.getUnits()) {
      agents.seize(1, javaSim.getClock());
      javaSim.schedulePlus("EndOfEntry", generator.normal(meanRegular, varRegular, 1), fax);
    } else {
      regularQ.add(fax, javaSim.getClock());
    }
  }

  private void endOfEntry(Entity departingFax) {
    double wait;

    // record wait time of regular; move in if special

    if (generator.uniform(0, 1, 2) < 0.2) {
      specialArrival(departingFax);
    } else {
      wait = javaSim.getClock() - departingFax.getCreateTime();
      regularWait.record(wait);
      if (wait < 10) {
        regular10.record(1);
      } else {
        regular10.record(0);
      }
    }

    // Check to see if there is another Fax; if yes start entry
    // otherwise free the agent

    if (regularQ.numQueue() > 0 && agents.getUnits() >= agents.getBusy()) {
      departingFax = (Entity) regularQ.remove(javaSim.getClock());
      javaSim.schedulePlus("EndOfEntry", generator.normal(meanRegular, varRegular, 1), departingFax);
    } else {
      agents.free(1, javaSim.getClock());
    }
  }

  private void specialArrival(Entity specialFax) {
    // if special agent available, start entry by seizing the special agent

    if (specialists.getBusy() < specialists.getUnits()) {
      specialists.seize(1, javaSim.getClock());
      javaSim.schedulePlus("EndOfEntrySpecial", generator.normal(meanSpecial, varSpecial, 3), specialFax);
    } else {
      specialQ.add(specialFax, javaSim.getClock());
    }
  }

  private void endOfEntrySpecial(Entity departingFax) {
    double wait;

    // record wait time and indicator if < 10 minutes
    wait = javaSim.getClock() - departingFax.getCreateTime();
    specialWait.record(wait);
    if (wait < 10) {
      special10.record(1);
    } else {
      special10.record(0);
    }

    // check to see if there is another Fax; if yes start entry
    // otherwise free the specialist

    if (specialQ.numQueue() > 0 && specialists.getUnits() >= specialists.getBusy()) {
      departingFax = (Entity) specialQ.remove(javaSim.getClock());
      javaSim.schedulePlus("EndOfEntrySpecial", generator.normal(meanSpecial, varSpecial, 3), departingFax);
    } else {
      specialists.free(1, javaSim.getClock());
    }

  }

  /**
   * Initialize the simulation
   */

  private void myInit() {
    String simulationName = "Fax Center";
    javaSim = new JavaSim(simulationName);

    // initialize the random number generator
    generator = new Rng();

    meanRegular = 2.5;
    varRegular = 1.0;
    meanSpecial = 4.0;
    varSpecial = 1.0;
    runLength = 480.0;

    numAgents = 15;
    numAgentsPM = 9;
    numSpecialists = 6;
    numSpecialistsPM = 3;

    // Add queues, resources and statistics that need to be
    // initialized between replications to the global collections

    regularWait = new DTStat();
    specialWait = new DTStat();
    regular10 = new DTStat();
    special10 = new DTStat();
    regularQ = new FIFOQueue(javaSim);
    specialQ = new FIFOQueue(javaSim);
    agents = new Resource(javaSim);
    specialists = new Resource(javaSim);

    javaSim.addDTStat(regularWait);
    javaSim.addDTStat(specialWait);
    javaSim.addDTStat(regular10);
    javaSim.addDTStat(special10);
    javaSim.addQueue(regularQ);
    javaSim.addQueue(specialQ);
    javaSim.addResource(agents);
    javaSim.addResource(specialists);

    javaSim.report("Ave Reg Wait", 0, 0);
    javaSim.report("Ave Num Reg Q", 0, 1);
    javaSim.report("Agents Busy", 0, 2);
    javaSim.report("Ave Spec Wait", 0, 3);
    javaSim.report("Ave Num Spec Q", 0, 4);
    javaSim.report("Specialists Busy", 0, 5);
    javaSim.report("Reg < 10", 0, 6);
    javaSim.report("Spec < 10", 0, 7);
    javaSim.report("End Time", 0, 8);

    // Arrival process data
    nPeriods = 8;
    period = 60.0;
    maxRate = 6.24;
    aRate = new double[8];
    aRate[0] = 4.37;
    aRate[1] = 6.24;
    aRate[2] = 5.29;
    aRate[3] = 2.97;
    aRate[4] = 2.03;
    aRate[5] = 2.79;
    aRate[6] = 2.36;
    aRate[7] = 1.04;

  }

  /**
   * This function generates interarrival times from a NSPP with piecewise
   * constant arrival rate over a fixed time of Period*NPeriod time units
   * 
   * @param aRate
   *          array of arrival rates over a common length Period
   * @param maxRate
   *          maximum value of ARate
   * @param nPeriods
   *          number of time periods in ARate
   * @param period
   *          time units between (possible) changes in arrival rate
   * @param stream
   *          seed for random number generator
   * @return
   */

  private double nsppFax(double[] aRate, double maxRate, int nPeriods, double period, int stream) {
    double possibleArrival = javaSim.getClock() + generator.expon(1.0 / maxRate, stream);
    int i = Math.min(nPeriods, (int) Math.ceil(possibleArrival / period));

    while(generator.uniform(0, 1, stream) >= aRate[i - 1] / maxRate) {
      possibleArrival += generator.expon(1.0 / maxRate, stream);
      i = Math.min(nPeriods, (int) Math.ceil(possibleArrival / period));
    }

    return possibleArrival - javaSim.getClock();
  }

  public static void main(String[] args) {
    FaxCenter faxCenterSim = new FaxCenter();
    faxCenterSim.runSimulation();
  }

}
