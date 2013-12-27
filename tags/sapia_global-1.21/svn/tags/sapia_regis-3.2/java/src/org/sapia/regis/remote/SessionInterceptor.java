package org.sapia.regis.remote;

import org.sapia.regis.Node;
import org.sapia.regis.RegisSession;
import org.sapia.regis.Registry;
import org.sapia.ubik.rmi.interceptor.Interceptor;
import org.sapia.ubik.rmi.server.invocation.ServerPostInvokeEvent;
import org.sapia.ubik.rmi.server.invocation.ServerPreInvokeEvent;

public class SessionInterceptor implements Interceptor{
  
  private Registry _regis;
  
  SessionInterceptor(Registry regis){
    _regis = regis;
  }
  
  public void onServerPreInvokeEvent(ServerPreInvokeEvent evt){
    RegistryServerLockManager.lock().readLock().lock();    
    if(evt.getTarget() instanceof Node){
      RegisSession sess = _regis.open();
      RemoteSessions.join(sess);
    }
  }
  
  public void onServerPostInvokeEvent(ServerPostInvokeEvent evt){
    RegistryServerLockManager.lock().readLock().unlock();    
    if(evt.getTarget() instanceof Node){
      RemoteSessions.close();
    }
  }  
  
}
