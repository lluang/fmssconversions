package org.javasim;

/**
 * 
 * @author ledelato Generic continuous-time statistics object. Note that CTStat
 *         should be called AFTER the value of the variable changes.
 */

public class CTStat {
  private double area;
  private double tLast;
  private double xLast;
  private double tClear;

  /**
   * Executes when CTStat object is created to initialize variables.
   */
  public CTStat() {
    area = 0.0;
    tLast = 0.0;
    tClear = 0.0;
    xLast = 0.0;
  }

  /**
   * Update the CTStat from last time change and keep track of previous value
   * 
   * @param x
   *          The value of the continuous time statistic
   * @param clock
   *          Simulation time when the statistic was recorded
   */

  public void record(double x, double clock) {
    area = area + xLast * (clock - tLast);
    tLast = clock;
    xLast = x;
  }

  /**
   * @param Current
   *          simulation time used to calculate the sample mean
   * @return the sample mean up through current time but do not update
   */
  public double mean(double clock) {
    double mean = 0;
    if (clock - tClear > 0) {
      mean = (area + xLast * (clock - tLast)) / (clock - tClear);
    }
    return mean;

  }

  /**
   * Clear statistics
   * 
   * @param clock
   *          Simulation time when the values are cleared
   */
  public void clear(double clock) {
    area = 0.0;
    tLast = clock;
    tClear = clock;
  }

}
