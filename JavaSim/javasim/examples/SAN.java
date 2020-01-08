package org.javasim.examples;

import java.util.ArrayList;
import java.util.List;

import org.javasim.Activity;
import org.javasim.EventNotice;
import org.javasim.JavaSim;
import org.javasim.Rng;

/**
 * SAN Simulation using a discrete-event approach
 */

public class SAN {
  // Nodes is a multi-dimensional ArrayList of ArrayList,
  // where each ArrayListis a list of inbound or outbound
  // activities to that node For Nodes(i, j) = inbound i=1
  // or outbound i=2 node j = 1 for a, j = 2 for b,
  // j = 3 for c, j = 4 for d.

  private ArrayList<ArrayList<ArrayList<Integer>>> nodes;

  private List<Integer>                            destination;

  // simulation object and the random number generator
  private JavaSim                                  javaSim;
  private Rng                                      generator;

  // constants
  private final int                                a     = 0;
  private final int                                b     = 1;
  private final int                                c     = 2;
  private final int                                d     = 3;
  private final int                                inTo  = 0;
  private final int                                outOf = 1;

  public SAN() {
    myInit();
  }

  /**
   * Run the SAN simulation and report output
   */

  private void runSimulation() {

    EventNotice nextEvent;
    Activity thisActivity;

    for(int rep = 0; rep < 1000; rep++) {
      javaSim.javaSimInit();
      sanInit(); // initializes the activities in the SAN
      milestone(0, a); // causes outbound activities of node a to be scheduled

      do {
        nextEvent = javaSim.calendarRemove();
        javaSim.setClock(nextEvent.getEventTime());
        thisActivity = (Activity) nextEvent.getWhichObject();
        milestone(thisActivity.getWhichActivity(), thisActivity.getWhichNode());
      } while(javaSim.calendarN() > 0); // stop when event calendar is empty

      javaSim.report(javaSim.getClock(), rep + 1, 0);
    }
  }

  /**
   * Initialize the simulation
   */

  private void myInit() {

    // initialize the random number generator
    generator = new Rng();

    // initialize the simulation
    String simulationName = "Discrete-Event Stochastic Activity Network";
    javaSim = new JavaSim(simulationName);

    javaSim.report("Completion Time", 0, 0);

  }

  /**
   * Initialize the activities in the SAN
   */

  private void sanInit() {
    // destinations
    // destination[i] corresponds to the destination of activity i

    // initialize the destination
    destination = new ArrayList<Integer>(5);

    // initialize the nodes
    nodes = new ArrayList<ArrayList<ArrayList<Integer>>>();
    for(int i = 0; i < 2; i++) {
      nodes.add(new ArrayList<ArrayList<Integer>>());
    }
    for(ArrayList<ArrayList<Integer>> node : nodes) {
      for(int j = 0; j < 4; j++) {
        node.add(new ArrayList<Integer>());
      }
    }

    destination.add(b);
    destination.add(c);
    destination.add(c);
    destination.add(d);
    destination.add(d);

    List<Integer> inbound = new ArrayList<Integer>();
    List<Integer> outbound = new ArrayList<Integer>();

    // node a
    outbound.add(0);
    outbound.add(1);
    nodes.get(inTo).get(a).addAll(inbound);
    nodes.get(outOf).get(a).addAll(outbound);
    inbound.clear();
    outbound.clear();

    // node b
    inbound.add(0);
    outbound.add(2);
    outbound.add(3);
    nodes.get(inTo).get(b).addAll(inbound);
    nodes.get(outOf).get(b).addAll(outbound);
    inbound.clear();
    outbound.clear();

    // node c
    inbound.add(1);
    inbound.add(2);
    outbound.add(4);
    nodes.get(inTo).get(c).addAll(inbound);
    nodes.get(outOf).get(c).addAll(outbound);
    inbound.clear();
    outbound.clear();

    // node d
    inbound.add(3);
    inbound.add(4);
    nodes.get(inTo).get(d).addAll(inbound);
    nodes.get(outOf).get(d).addAll(outbound);
    inbound.clear();
    outbound.clear();

  }

  /**
   * Schedule a milestone, an activity 'actIn' inbound to node 'node' to occur x
   * ~ expon(1) time units later
   * 
   * @param actIn
   *          Inbound activity
   * @param node
   *          Node at which mile stone happens
   */

  private void milestone(int actIn, int node) {
    int m;
    ArrayList<Integer> inbound = new ArrayList<Integer>(nodes.get(inTo).get(node));
    ArrayList<Integer> outbound = new ArrayList<Integer>(nodes.get(outOf).get(node));
    m = inbound.size();

    for(int incoming = 0; incoming < m; incoming++) {
      if (inbound.get(incoming) == actIn) {
        inbound.remove(incoming);
        break;
      }
    }
    nodes.get(inTo).get(node).clear();
    nodes.get(inTo).get(node).addAll(inbound);

    if (inbound.isEmpty()) {
      m = outbound.size();
      for(int actOut = 0; actOut < m; actOut++) {
        Activity thisActivity = new Activity();
        thisActivity.setWhichActivity(outbound.get(0));
        thisActivity.setWhichNode(destination.get(outbound.get(0)));
        javaSim.schedulePlus("Milestone", generator.expon(1, 0), thisActivity);
        outbound.remove(0);
      }
    }

  }

  /**
   * Initialize and run the SAN simulation
   * 
   * @param args
   */

  public static void main(String[] args) {
    SAN sanSimulation = new SAN();
    sanSimulation.runSimulation();

  }

}
