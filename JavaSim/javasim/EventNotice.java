/**
 *  This is a generic EventNotice object with EventTime, EventType 
 *  and WhichObject attributes
 */

//This is a generic EventNotice object with EventTime and EventType attributes

package org.javasim;

public class EventNotice {
  private double eventTime;
  private String eventType;
  private Object whichObject;

  /**
   * Event with time and type.
   * 
   * @param eventTime
   *          Time of event
   * @param eventType
   *          String representing event
   */

  public EventNotice(double eventTime, String eventType) {
    this.eventTime = eventTime;
    this.eventType = eventType;
  }

  /**
   * Event with time, type and an object.
   * 
   * @param eventTime
   *          Time of event
   * @param eventType
   *          String representing event
   * @param whichObject
   *          object associated with event
   */

  public EventNotice(double eventTime, String eventType, Object whichObject) {
    this.eventTime = eventTime;
    this.eventType = eventType;
    this.whichObject = whichObject;
  }

  public double getEventTime() {
    return eventTime;
  }

  public String getEventType() {
    return eventType;
  }

  public Object getWhichObject() {
    return whichObject;
  }

  // Add additional problem specific attributes here

}
