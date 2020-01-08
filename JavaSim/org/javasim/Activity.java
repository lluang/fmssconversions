package org.javasim;

/**
 * Object to model an activity-destination node pair
 */

public class Activity {
  private int whichActivity;
  private int whichNode;

  /**
   * 
   * @param whichActivity
   *          Index for the activity
   * @param whichNode
   *          Index for the node
   */

  public void activity(int whichActivity, int whichNode) {
    this.whichActivity = whichActivity;
    this.whichNode = whichNode;
  }

  public int getWhichActivity() {
    return whichActivity;
  }

  public int getWhichNode() {
    return whichNode;
  }

  public void setWhichActivity(int whichActivity) {
    this.whichActivity = whichActivity;
  }

  public void setWhichNode(int whichNode) {
    this.whichNode = whichNode;
  }

}
