package org.sapia.soto.ubik.monitor;


/**
 * An instance of this interface manages <code>MonitorAgent</code>s.
 * 
 * @see org.sapia.soto.ubik.monitor.MonitorAgent
 * @see StatusResult
 * 
 * @author Yanick Duchesne
 *
 */
public interface Monitor {
  
  /**
   * @param serviceId the identifier of the Soto services whose status is
   * to be checked.
   * @return a <code>StatusResultList</code> of <code>StatusResult</code> instances.
   * @see StatusResult
   */
  public StatusResultList getStatusForId(String serviceId);

  /**
   * @param className the class name of the Soto services whose status is
   * to be checked.
   * @return a <code>StatusResultList</code> of <code>StatusResult</code> instances.
   * @see StatusResult
   */  
  public StatusResultList getStatusForClass(String className);
  
}
