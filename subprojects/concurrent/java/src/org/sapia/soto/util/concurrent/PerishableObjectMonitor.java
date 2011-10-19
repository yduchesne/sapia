package org.sapia.soto.util.concurrent;


/**
 *
 * @author Jean-Cédric Desrochers
 */
public interface PerishableObjectMonitor {

  /**
   * Adds a listener on this perishable object monitor.
   * 
   * @param aListener The new perishable object monitor listener to add.
   */
  public void addListener(PerishableObjectMonitorListener aListener);
  
  /**
   * Removes a listener from this perishable object monitor.
   * 
   * @param aListener The perishable object monitor listener to remove.
   * @return True if the listener was removed, false otherwise.
   */
  public boolean removeListener(PerishableObjectMonitorListener aListener);
  
  /**
   * Registers the perishable object passed in with this monitor. The validity value
   * will be calculated using the time to live of the perishable object.
   * 
   * @param aPerishableObject The perishable object to register.
   */
  public void register(PerishableObject aPerishableObject);
  
  /**
   * Renew the perishable object that is already registered with this monitor.
   * 
   * @param aPerishableObject The object to renew.
   */
  public void renew(PerishableObject aPerishableObject);
  
  /**
   * Renew the perishable objects that are already registered with this monitor.
   * This version performs all the changes at once to improve efficiency.
   * 
   * @param somePerishableObjecte The objects to renew.
   */
  public void renew(PerishableObject[] somePerishableObjects);
  
  /**
   * Unregisters the perishable object passed in fro this monitor.
   * 
   * @param aPerishableObject The perishable object to unregister.
   * @return True if the object was unregistered with success, false otherwise.
   */
  public boolean unregister(PerishableObject aPerishableObject);
  
  /**
   * Refresh the object validity of this monitor. Called when the time to live period 
   * of a persihable object already registered with the monitor changed.
   */
  public void refreshPerishableObjects();

}
