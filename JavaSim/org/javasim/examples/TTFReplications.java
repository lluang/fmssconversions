package org.javasim.examples;

import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class TTFReplications {

  private double clock;      // simulation clock
  private double nextFailure; // time of next failure event
  private double nextRepair; // time of next repair event
  private double s;          // system state
  private double sLast;      // previous value of the system state
  private double tLast;      // time of previous state change
  private double area;       // area under S(t) curve
  private Random generator;  // Java random number generator

  /**
   * Method to generate a sample path for the TTF example
   */

  private void ttfRep() {
    String nextEvent;
    final int infinity = 1000000;

    double sumS = 0.0;
    double sumY = 0.0;

    int nReps = 100;

    for(int rep = 0; rep < nReps; rep++) {

      // Initialize the state and statistical variables
      s = 2.0;
      sLast = 2.0;
      clock = 0.0;
      tLast = 0.0;
      area = 0.0;

      // Schedule the initial failure event
      nextFailure = Math.ceil(6 * generator.nextDouble());
      nextRepair = infinity;

      // Advance time and execute events until the system fails

      while(s > 0) {
        nextEvent = timer();
        if (nextEvent == "Failure") {
          failure();
        } else if (nextEvent == "Repair") {
          repair();
        }
      }

      // accumulate replication statistics
      sumS = sumS + area / clock;
      sumY = sumY + clock;

    }
    JFrame frame = new JFrame("TestTheDialog Tester");
    JOptionPane.showMessageDialog(frame, "Average failure at time " + sumY / (double) nReps + " with average # functional components " + sumS / (double) nReps);
  }

  private String timer() {
    final int infinity = 1000000;
    // determine the next event and advance time
    if (nextFailure < nextRepair) {
      clock = nextFailure;
      nextFailure = infinity;
      return "Failure";
    } else {
      clock = nextRepair;
      nextRepair = infinity;
      return "Repair";
    }
  }

  private void failure() {
    // Failure event
    // Update state and schedule future events
    s--;
    if (s == 1) {
      nextFailure = clock + Math.ceil(6.0 * generator.nextDouble());
      nextRepair = clock + 2.5;
    }

    // update area under the S(t) curve
    area = area + sLast * (clock - tLast);
    tLast = clock;
    sLast = s;
  }

  private void repair() {
    // repair event
    // update state and schedule future events
    s++;
    if (s == 1) {
      nextRepair = clock + 2.5;
      nextFailure = clock + Math.ceil(6.0 * generator.nextDouble());
    }

    // Update area under the S(t) curve
    area = area + sLast * (clock - tLast);
    tLast = clock;
    sLast = s;
  }

  public TTFReplications() {
    generator = new Random(1234);
  }

  public static void main(String[] args) {
    TTFReplications ttfReps = new TTFReplications();
    ttfReps.ttfRep();
  }

}
