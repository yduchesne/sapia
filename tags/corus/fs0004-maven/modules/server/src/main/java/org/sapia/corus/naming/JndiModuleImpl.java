package org.sapia.corus.naming;

import javax.naming.Context;
import javax.naming.NamingException;

import org.sapia.corus.CorusRuntime;
import org.sapia.corus.ModuleHelper;
import org.sapia.corus.ServerStartedEvent;
import org.sapia.corus.admin.services.naming.JndiModule;
import org.sapia.corus.cluster.ClusterManager;
import org.sapia.corus.event.EventDispatcher;
import org.sapia.ubik.mcast.EventChannel;
import org.sapia.ubik.rmi.interceptor.Interceptor;
import org.sapia.ubik.rmi.naming.remote.ClientListener;
import org.sapia.ubik.rmi.naming.remote.JNDIServerHelper;
import org.sapia.ubik.rmi.naming.remote.RemoteContext;


/**
 * This class implements a remote JNDI provider.
 * 
 * @author yduchesne
 */
public class JndiModuleImpl extends ModuleHelper implements JndiModule, Interceptor{
  private Context _context;
  private ClientListener _listener;
  
  /**
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
    EventChannel ec = ((ClusterManager)super.lookup(ClusterManager.class)).getEventChannel();
    ((EventDispatcher)super.lookup(EventDispatcher.class)).addInterceptor(ServerStartedEvent.class, this);
    _context = JNDIServerHelper.newRootContext(ec);
  }
  
  /**
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
    try{
     _context.close();
    }catch(NamingException e){}
    
  }
  
  /*////////////////////////////////////////////////////////////////////
                          Module INTERFACE METHODS
  ////////////////////////////////////////////////////////////////////*/
  
  /**
   * @see org.sapia.corus.admin.Module#getRoleName()
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
    return _context;
  }
  
  /**
   * This method is called once the corus server in which this instance
   * lives has started listening to requests.
   *
   * @param evt a <code>ServerStartedEvent</code> instance.
   */
  public void onServerStartedEvent(ServerStartedEvent evt){
    try{
      EventChannel ec = ((ClusterManager)super.lookup(ClusterManager.class)).getEventChannel();
      CorusRuntime.getTransport().exportObject(_context);
      _listener = JNDIServerHelper.createClientListener(ec, CorusRuntime.getTransport().getServerAddress());
    }catch(Exception e){
      logger().error("Could not initialize client listener properly in JNDI module", e);
    }
  }
  
  public RemoteContext getRemoteContext(){
    return (RemoteContext)_context;
  }
  
}
