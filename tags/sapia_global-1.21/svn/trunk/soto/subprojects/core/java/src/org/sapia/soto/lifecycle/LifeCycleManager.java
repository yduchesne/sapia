package org.sapia.soto.lifecycle;

import org.sapia.soto.Env;
import org.sapia.soto.ServiceConfiguration;

public interface LifeCycleManager {
  
  /**
   * This method is called on this instance when the Soto container's init() method is
   * called, prior to initializing any service.
   * 
   * @param env the {@link Env} instance corresponding to the Soto container
   * in the context of which this manager is used.
     */
  public void init(Env env);
  
  /**
   * This method is called on this instance when the Soto container's init() method is
   * called, after all services have been initialized.
   * 
   * @param env the {@link Env} instance corresponding to the Soto container
   * in the context of which this manager is used.
   * @throws Exception if a problem occurs.
   */  
  public void postInit(Env env);
  
  /**
   * This method is called on this instance when the Soto container's dispose() method
   * is called, after all services have been initialized.
   * 
   * @param env the {@link Env} instance corresponding to the Soto container
   * in the context of which this manager is used.
   */  
  public void dispose(Env env); 

  /**
   * This method is called by the Soto container when a given service's initialization
   * method is to be called.
   * 
   * @param conf a {@link ServiceConfiguration} instance.
   * @throws Exception if a problem occurs while performing the initialization.
   */
  public void initService(ServiceConfiguration conf) throws Exception;  

  /**
   * This method is called by the Soto container when a given service's startup
   * method is to be called.
   * @param conf a {@link ServiceConfiguration} instance.
   * @throws Exception if a problem occurs while performing the startup. 
   */
  public void startService(ServiceConfiguration conf) throws Exception;

  /**
   * This method is called by the Soto container when a service has been successfully looked up.
   *  
   * @param name the name used by the client application when performing the lookup, or <code>null</code>
   * if the lookup was not performed by name.
   * @param service the service object that was found.
   * @return the service object to return to the client application.
   */
  public Object lookupService(String name, Object service);
  
  /**
   * This method is called by lookup code that searches services based on their
   * type.
   * 
   * @param service a service instance.
   * 
   * @return the {@link Class} corresponding to the type of the service.
   */
  public Class getServiceClass(Object service);

  /**
   * This method is called by the Soto container when a given service's dispose/destroy
   * method is to be called.
   * @param conf a {@link ServiceConfiguration} instance.
   * @throws Exception if a problem occurs while performing the disposal/destruction. 
   */  
  public void disposeService(ServiceConfiguration conf) throws Exception;  
}
