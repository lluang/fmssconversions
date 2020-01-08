/**
 * This class module creates an Event Calendar object 
 * which is a list of event notices ordered by event time. 
 * Based on an object created by Steve Roberts @ NCSU
 */

package org.javasim;

import java.util.ArrayList;
import java.util.List;

public class EventCalendar {

  private List<EventNotice> thisCalendar;

  /**
   * Constructor Initialize the thisCalendar list
   */

  public EventCalendar() {
    thisCalendar = new ArrayList<EventNotice>();
  }

  /**
   * Add EventNotice in EventTime order
   * 
   * @param addedEvent
   *          EventNotice to add
   */

  public void schedule(EventNotice addedEvent) {

    int i;

    if (thisCalendar.isEmpty()) {// no events in calendar
      thisCalendar.add(addedEvent);
    } else if (thisCalendar.get(thisCalendar.size() - 1).getEventTime() <= addedEvent.getEventTime()) {
      // added event after last event in calendar
      thisCalendar.add(addedEvent);
    } else { // search for the correct place to insert the event
      for(i = 0; i < thisCalendar.size(); i++) {
        if (thisCalendar.get(i).getEventTime() > addedEvent.getEventTime()) {
          break;
        }
      }

      thisCalendar.add(i, addedEvent);
    }
  }

  /**
   * Remove next event and return the EventNotice object
   * 
   * @return next event
   */

  public EventNotice remove() {
    if (!thisCalendar.isEmpty()) {
      return thisCalendar.remove(0);
    }
    return null;
  }

  /**
   * Return current number of events on the event calendar
   * 
   * @return current number of events on the event calendar
   */

  public int N() {
    return thisCalendar.size();
  }

}
