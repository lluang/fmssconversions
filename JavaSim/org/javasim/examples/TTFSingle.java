package org.javasim.examples;

import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class TTFSingle {
  // Program to generate a sample path for the TTF example
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

  private void ttf() {
    String nextEvent;
    final int infinity = 1000000;

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
    JFrame frame = new JFrame("Time To Failure Simulation Output");
    JOptionPane.showMessageDialog(frame, "System failure at time " + clock + " with average # functional components " + area / clock);
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

  public TTFSingle() {
    generator = new Random(1234);
  }

  public static void main(String[] args) {
    TTFSingle ttfSingle = new TTFSingle();
    ttfSingle.ttf();
  }

}
