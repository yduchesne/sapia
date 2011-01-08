package org.sapia.corus.event;

import org.apache.log.Hierarchy;


import org.apache.log.Logger;
import org.sapia.corus.client.annotations.Bind;
import org.sapia.corus.client.services.event.EventDispatcher;
import org.sapia.corus.core.Service;
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
   * @see org.sapia.corus.client.Module#getRoleName()
   */
  public String getRoleName() {
    return ROLE;
  }
  
  /**
   * @see org.sapia.corus.core.soto.Service#init()
   */
  public void init() throws Exception {}
  
  /**
   * @see org.sapia.corus.core.soto.Service#start()
   */
  public void start() throws Exception {}
  
  /**
   * @see org.sapia.corus.core.soto.Service#dispose()
   */
  public void dispose() {}
  
  @Override
  public void addInterceptor(Class event, Interceptor it){
    logger.debug("Adding interceptor: " + it + " for event type: " + event);
    super.addInterceptor(event, it);
  }

}