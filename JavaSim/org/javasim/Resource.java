package org.javasim;

/**
 * This is a generic Resource object that also keeps track of statistics on the
 * number of busy resources
 */

public class Resource {

  private int    busy;
  private int    numberOfUnits;
  private CTStat numBusy;

  /**
   * Executes when resource object is created to initialize variables and add
   * number of busy Resources statistic to TheCTStats collection
   * 
   * @param theCTStats
   *          continous time statistics to record
   */

  public Resource(JavaSim javaSim) {
    busy = 0;
    numberOfUnits = 0;
    numBusy = new CTStat();
    javaSim.addCTStat(numBusy);
  }

  /**
   * Seize Units of resource then updates statistics Returns False and does not
   * seize if not enough resources available; otherwise returns True
   * 
   * @param units
   * @param clock
   * @return
   */

  public boolean seize(int units, double clock) {
    int diff = numberOfUnits - units - busy;
    if (diff >= 0) {
      // If diff is nonnegative, then there are enough resources to seize
      busy += units;
      numBusy.record(busy, clock);
      return true;
    }
    return false;
  }

  /**
   * Free Units of resource then updates statistics
   * 
   * @param units
   *          Units of resource to free
   * @param clock
   *          Simulation time when units are freed
   * @return Returns false and does not free if attempting to free more
   *         resources than available; otherwise returns true
   */

  public boolean free(int units, double clock) {
    int diff = busy - units;
    if (diff < 0) {
      return false;
    }
    busy -= units;
    numBusy.record(busy, clock);
    return true;
  }

  /**
   * Return time-average number of busy resources up to current time
   * 
   * @param clock
   *          Current simulation time
   * @return Return time-average number of busy resources up to current time
   */

  public double mean(double clock) {
    return numBusy.mean(clock);
  }

  /**
   * Set the capacity of the resource (number of identical units)
   * 
   * @param units
   *          number of identical units to set
   */

  public void setUnits(int units) {
    numberOfUnits = units;
  }

  /**
   * @return Number of units
   */

  public int getUnits() {
    return numberOfUnits;
  }

  /**
   * Set the number of busy resources
   * 
   * @param nBusy
   *          number of busy resources to set
   */

  public void setBusy(int nBusy) {
    busy = nBusy;
  }

  /**
   * 
   * @return the number of busy units
   */

  public int getBusy() {
    return busy;
  }

}
