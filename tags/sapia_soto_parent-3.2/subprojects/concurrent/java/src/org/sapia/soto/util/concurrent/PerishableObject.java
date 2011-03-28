package org.sapia.soto.util.concurrent;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public interface PerishableObject {

  /** Defines a value for an infinite time to live. */
  public static final long INFINITE_TIME_TO_LIVE = -1;

  /**
   * Returns the unique identifier of this perishable object.
   *  
   * @return The unique identifier of this perishable object.
   */
  public long getObjectId();
  
  /**
   * Returns the time to live value of this perishable object in milliseconds.
   * 
   * @return The time to live value of this perishable object in milliseconds.
   */
  public long getTimeToLiveMillis();
}
