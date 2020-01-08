package org.javasim;

/**
 * This is a generic entity that has a single attribute CreateTime
 */

public class Entity {
  private final double createTime;

  /**
   * Executes when entity object is created to initialize variables
   * 
   * @param clock
   *          Simulation time at the time of entity's creation.
   */

  public Entity(double clock) {
    this.createTime = clock;
  }

  /**
   * @return Create time of this entity
   */

  public double getCreateTime() {
    return createTime;
  }

}
