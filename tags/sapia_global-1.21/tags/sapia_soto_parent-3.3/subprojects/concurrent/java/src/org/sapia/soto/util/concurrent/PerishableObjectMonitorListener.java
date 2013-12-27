package org.sapia.soto.util.concurrent;


/**
 *
 * @author Jean-Cédric Desrochers
 */
public interface PerishableObjectMonitorListener {

  /**
   * Called by the perishable object monitor when a perishable object has reach is time to live (end of life).
   * 
   * @param aPerishableObject The perishable object that is expired.
   */
  public void onExpiredObject(PerishableObject aPerishableObject);

}
