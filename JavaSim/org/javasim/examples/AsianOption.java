package org.javasim.examples;

import org.javasim.JavaSim;
import org.javasim.Rng;

/**
 * Simulation of an Asian Option
 */

public class AsianOption {

  public static void main(String[] args) {
    int replications = 10000;
    double maturity = 1.0;
    int steps = 32;
    double sigma = 0.3;
    double interestRate = 0.05;
    double initialValue = 50.0;
    double strikePrice = 55.0;
    double interval = maturity / (double) steps;
    double sigma2 = sigma * sigma / 2.0;

    double x;
    double sum;
    double z;
    double value;

    // random number generator for the simulation
    Rng generator = new Rng();

    // Simulation object for the simulations
    String simulationName = "Asian Option";
    JavaSim simObject = new JavaSim(simulationName);

    simObject.report("Option Value", 0, 0);

    for(int i = 0; i < replications; i++) {
      sum = 0.0;
      x = initialValue;
      for(int j = 0; j < steps; j++) {
        z = generator.normal(0, 1, 11);
        x = x * Math.exp((interestRate - sigma2) * interval + sigma * Math.sqrt(interval) * z);
        sum = sum + x;
      }
      value = Math.exp(-interestRate * maturity) * Math.max(sum / (double) steps - strikePrice, 0.0);
      simObject.report(value, i + 1, 0);
    }

  }
}