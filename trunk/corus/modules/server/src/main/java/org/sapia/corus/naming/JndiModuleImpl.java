package org.sapia.corus.naming;

import javax.naming.Context;
import javax.naming.NamingException;

import org.sapia.corus.client.annotations.Bind;
import org.sapia.corus.client.services.Service;
import org.sapia.corus.client.services.cluster.ClusterManager;
import org.sapia.corus.client.services.event.EventDispatcher;
import org.sapia.corus.client.services.naming.JndiModule;
import org.sapia.corus.core.ModuleHelper;
import org.sapia.corus.core.ServerStartedEvent;
import org.sapia.ubik.mcast.EventChannel;
import org.sapia.ubik.rmi.Remote;
import org.sapia.ubik.rmi.interceptor.Interceptor;
import org.sapia.ubik.rmi.naming.remote.ClientListener;
import org.sapia.ubik.rmi.naming.remote.JNDIServerHelper;
import org.sapia.ubik.rmi.naming.remote.RemoteContext;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * This class implements a remote JNDI provider.
 * 
 * @author yduchesne
 */
@Bind(moduleInterface=JndiModule.class)
@Remote(interfaces=JndiModule.class)
public class JndiModuleImpl extends ModuleHelper implements JndiModule, Interceptor{

  @Autowired
  EventDispatcher 			events;
  @Autowired
  ClusterManager 				 cluster;
  private Context 			 context;
  
  // keeping reference to avoid garbage-collection
  @SuppressWarnings("unused")
  private ClientListener listener;
  
  /**
   * @see Service#init()
   */
  public void init() throws Exception {
    EventChannel ec = cluster.getEventChannel();
    events.addInterceptor(ServerStartedEvent.class, this);
    context = JNDIServerHelper.newRootContext(ec);
  }
  
  /**
   * @see Service#dispose()
   */
  public void dispose() {
    try{
    	context.close();
    }catch(NamingException e){}
    
  }
  
  /*////////////////////////////////////////////////////////////////////
                          Module INTERFACE METHODS
  ////////////////////////////////////////////////////////////////////*/
  
  /**
   * @see org.sapia.corus.client.Module#getRoleName()
   */
  public String getRoleName() {
    return JndiModule.ROLE;
  }
  
  /*////////////////////////////////////////////////////////////////////
                        JndiModule INTERFACE METHODS
  ////////////////////////////////////////////////////////////////////*/
  
  /**
   * @see JndiModule#getContext()
   */
  public Context getContext() {
    return context;
  }
  
  /**
   * This method is called once the corus server in which this instance
   * lives has started listening to requests.
   *
   * @param evt a <code>ServerStartedEvent</code> instance.
   */
  public void onServerStartedEvent(ServerStartedEvent evt){
    try{
      EventChannel ec = cluster.getEventChannel();
      serverContext().getTransport().exportObject(context);
      listener = JNDIServerHelper.createClientListener(ec, serverContext().getTransport().getServerAddress());
    }catch(Exception e){
      logger().error("Could not initialize client listener properly in JNDI module", e);
    }
  }
  
  public RemoteContext getRemoteContext(){
    return (RemoteContext) context;
  }
  
}
