package org.javasim;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a generic FIFO Queue object that also keeps track of statistics on
 * the number in queue (WIP)
 */

public class FIFOQueue {
  private List<Entity> thisQueue;
  CTStat               wip;

  /**
   * Executes when FIFOQueue object is created to add queue statistics to
   * TheCTStats collection
   * 
   * @param arrayList
   *          Collection of continuous-time statistics.
   */

  public FIFOQueue(JavaSim javaSim) {
    wip = new CTStat();
    thisQueue = new ArrayList<Entity>();
    javaSim.addCTStat(wip);
  }

  /**
   * @return Return current number in queue
   */

  public int numQueue() {
    return thisQueue.size();
  }

  /**
   * Add an entity to the end of the queue
   * 
   * @param thisEntity
   *          Entity to add to the end of the queue
   * @param clock
   *          Simulation time at which the entity is added the queue
   */

  public void add(Entity thisEntity, double clock) {
    thisQueue.add(thisEntity);
    wip.record(thisQueue.size(), clock);
  }

  /**
   * Remove the first entity from the queue and return the object after updating
   * queue statistics
   * 
   * @param clock
   *          the system time at which the entity is removed
   * @return The first entity from the queue
   */

  public Object remove(double clock) {

    Entity returnObj = null;
    if (!thisQueue.isEmpty()) {
      returnObj = thisQueue.get(0);
      thisQueue.remove(0);
    }
    wip.record(thisQueue.size(), clock);

    return returnObj;
  }

  /**
   * Return the average number in queue up to the current time
   * 
   * @param clock
   *          Current simulation time
   */

  public double mean(double clock) {
    return wip.mean(clock);
  }
}
