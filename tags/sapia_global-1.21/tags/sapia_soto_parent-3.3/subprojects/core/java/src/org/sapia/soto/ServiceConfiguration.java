package org.sapia.soto;

public interface ServiceConfiguration {
  
  /**
   * @return the service that this instance encapsulates.
   */
  public Object getService();
  
  /**
   * @return the {@link Class} corresponding to the type of service.
   */
  public Class getServiceClass();
  
  /**
   * This method allows substituting the current service of this instance with another one.
   * It is meant for specific uses (such as proxying), and should not be called inadvertently.
   * @param service a service object.
   */
  public void setService(Object service);
  
  /**
   * @return the ID of the service that this instance encapsulates - or <code>null</code> if no
   * ID was assigned.
   */
  public String getServiceID();
  
  /**
   * Invokes the "init" method on the service encapsulated by this instance (if such an init method
   * was specified).
   * @throws Exception if a problem occurs.
   */
  public void invokeInitMethod() throws Exception;
  
  /**
   * Invokes the "start" method on the service encapsulated by this instance (if such a start method
   * was specified).
   * @throws Exception if a problem occurs.
   */  
  public void invokeStartMethod() throws Exception;  
  
  /**
   * Invokes the "dispose" method on the service encapsulated by this instance (if such a dispose method
   * was specified).
   * @throws Exception if a problem occurs.
   */  
  public void invokeDisposeMethod();  

}
