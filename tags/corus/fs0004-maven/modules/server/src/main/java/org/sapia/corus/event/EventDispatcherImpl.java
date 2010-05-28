package org.sapia.corus.event;

import org.apache.log.Hierarchy;

import org.apache.log.Logger;
import org.sapia.corus.Service;
import org.sapia.corus.annotations.Bind;
import org.sapia.ubik.rmi.interceptor.Interceptor;
import org.sapia.ubik.rmi.interceptor.MultiDispatcher;

/**
 * Implements the {@link EventDispatcher} interface.
 * 
 * @author Yanick Duchesne
 */
@SuppressWarnings(value="unchecked")
@Bind(moduleInterface=EventDispatcher.class)
public class EventDispatcherImpl extends MultiDispatcher implements EventDispatcher, Service{
  
  private Logger logger = Hierarchy.getDefaultHierarchy().getLoggerFor("EventDispatcher");
  
  /**
   * @see org.sapia.corus.admin.Module#getRoleName()
   */
  public String getRoleName() {
    return ROLE;
  }
  
  /**
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {}
  
  /**
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {}
  
  /**
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {}
  
  @Override
  public void addInterceptor(Class event, Interceptor it){
    logger.debug("Adding interceptor: " + it + " for event type: " + event);
    super.addInterceptor(event, it);
  }

}
