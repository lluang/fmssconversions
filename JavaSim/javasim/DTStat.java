package org.javasim;

/**
 * 
 * Generic discrete-time statistics object.
 * 
 */

public class DTStat {
  private double sum;
  private double sumSquared;
  private double numberOfObservations;

  /**
   * 
   * Executes when DTStat object is created to initialize variables.
   * 
   */

  public DTStat() {
    sum = 0.0;
    sumSquared = 0.0;
    numberOfObservations = 0.0;
    numberOfObservations = 0.0;
  }

  /**
   * Update the DTStat
   * 
   * @param x
   *          The value of the discrete time statistic
   */

  public void record(double x) {
    sum += x;
    sumSquared += x * x;
    numberOfObservations++;
  }

  /**
   * 
   * @return the sample mean
   */

  public double mean() {
    double mean = sum / numberOfObservations;
    return mean;
  }

  /**
   * 
   * @return the sample standard deviation
   */

  public double stdDev() {
    if (numberOfObservations > 1.0) {
      return Math.sqrt((sumSquared - sum * sum / numberOfObservations) / (numberOfObservations - 1.0));
    }
    return 0;
  }

  /**
   * Clear statistics
   */

  public void clear() {
    sum = 0.0;
    sumSquared = 0.0;
    numberOfObservations = 0.0;
  }
}
