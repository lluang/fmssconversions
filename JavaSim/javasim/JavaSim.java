/**
 * THIS IS JAVASIM
 * JavaSim is a minimal collection of functions to support
 * discrete-event simulation programming in Java.
 *  Random-number and variate generation routines borrowed from simlib (Law 2007)
 * and translated into Java by Luis de la Torre
 */

package org.javasim;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JFrame;

public class JavaSim {

  private double                clock;         // simulation global clock
  private EventCalendar         calendar;      // event calendar

  private String                simulationName; // string used to display the
                                                // name of the simulation in the
                                                // output table

  // Set up Collections to be reinitialized between replications

  private Collection<CTStat>    theCTStats;    // continuous-time statistics
  private Collection<DTStat>    theDTStats;    // discrete-time statistics
  private Collection<FIFOQueue> theQueues;     // queues
  private Collection<Resource>  theResources;  // resources

  private OutputTable           outputDisplay; // the table object (JTable) to

  // write to and display output

  /**
   * Function to initialize VBASim Typically called before the first replication
   * and between replications
   */

  public void javaSimInit() {

    clock = 0.0;

    while(calendar.N() > 0) {
      calendar.remove();
    }

    // Empty queues

    for(FIFOQueue q : theQueues) {
      while(q.numQueue() > 0) {
        q.remove(clock);
      }
    }

    // clear statistics

    for(CTStat ct : theCTStats) {
      ct.clear(clock);
    }

    for(DTStat dt : theDTStats) {
      dt.clear();
    }

    // reinitialize the resources to idle

    for(Resource re : theResources) {
      re.setBusy(0);
    }

  }

  /**
   * Constructor Initialize the collections of statistics, queues, and resources
   */

  public JavaSim(String simulationName) {
    theCTStats = new ArrayList<CTStat>();
    theDTStats = new ArrayList<DTStat>();
    theQueues = new ArrayList<FIFOQueue>();
    theResources = new ArrayList<Resource>();
    calendar = new EventCalendar();
    this.simulationName = new String(simulationName);

    JFrame frame = new JFrame("Java Sim Output " + simulationName);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Create and set up the content pane.
    outputDisplay = new OutputTable(12, 15000);
    outputDisplay.setOpaque(true); // content panes must be opaque
    frame.setContentPane(outputDisplay);

    // Display the window.
    frame.pack();
    frame.setVisible(true);

    javaSimInit();
  }

  /**
   * Schedule future events of EventType to occur at time Clock + EventTime
   * 
   * @param eventType
   *          String representing the type of event
   * @param eventTime
   *          number of simulation time units from current time at which the
   *          event will occur
   */

  public void schedule(String eventType, double eventTime) {

    // insert problem specific event notice attributes here

    calendar.schedule(new EventNotice(clock + eventTime, eventType));
  }

  public void report(Object output, int row, int column) {
    outputDisplay.table.setValueAt(output, row, column);
    outputDisplay.table.getRowCount();
  }

  /**
   * Schedule future events of EventType to occur at time Clock + EventTime and
   * pass TheObject
   * 
   * @param eventType
   *          String representing the type of event
   * @param eventTime
   *          number of simulation time units from current time at which the
   *          event will occur
   * @param object
   *          associated wit the scheduled event
   */

  public void schedulePlus(String eventType, double eventTime, Object theObject) {
    EventNotice addedEvent = new EventNotice(clock + eventTime, eventType, theObject);
    calendar.schedule(addedEvent);
  }

  /**
   * Clear statistics in TheDTStats and TheCTStats
   */

  public void clearStats() {
    for(CTStat ct : theCTStats) {
      ct.clear(clock);
    }

    for(DTStat dt : theDTStats) {
      dt.clear();
    }
  }

  /**
   * add a discrete time statistic to theDTStats
   * 
   * @param dtStat
   *          discrete time statistic to add to theDTStats
   */
  public void addDTStat(DTStat dtStat) {
    theDTStats.add(dtStat);
  }

  /**
   * add a continuous time statistic to theCTStats
   * 
   * @param ctStat
   *          continuous time statistic to add to theCTStats
   */
  public void addCTStat(CTStat ctStat) {
    theCTStats.add(ctStat);
  }

  /**
   * @return the next EventNotice on the calendar
   */

  public EventNotice calendarRemove() {
    return calendar.remove();
  }

  /**
   * @return the current clock time
   */

  public double getClock() {
    return clock;
  }

  /**
   * Set the clock time to clock
   * 
   * @param clock
   */

  public void setClock(double clock) {
    this.clock = clock;
  }

  /**
   * Add a FifoQueue to theQueues
   * 
   * @param queue
   *          FifoQueue to add to theQueues
   */

  public void addQueue(FIFOQueue queue) {
    theQueues.add(queue);
  }

  /**
   * Add a resource to theResources
   * 
   * @param resource
   *          to add to theResources
   */

  public void addResource(Resource resource) {
    theResources.add(resource);
  }

  /**
   * @return number of events on the event calendar
   */

  public int calendarN() {
    return calendar.N();
  }

  public static void main(String[] args) {
    System.out.println("Usage:");
    System.out.println("To run code from command line, use command java -cp javaSim.jar org.javasim.examples.<ExampleName>");
    System.out.println("Example names:");
    System.out.println("AsianOption");
    System.out.println("FaxCenter");
    System.out.println("MG1");
    System.out.println("MG1Lindley");
    System.out.println("MTMInfinity");
    System.out.println("SAN");
    System.out.println("SANMax");
    System.out.println("TTFSingle");
    System.out.println("TTFReplications");
    System.out.println();
    System.out.println("JAR file includes source code.");

  }
}
