package org.sapia.regis.cache;

import org.sapia.regis.RegisSession;

public class CacheSessions {
  
  private static ThreadLocal sessions = new ThreadLocal();
    
  public static RegisSession get(){
    if(sessions.get() == null){
      throw new IllegalStateException("Server thread not registered with session");
    }
    return (RegisSession)sessions.get();
  }

  public static void join(RegisSession session){
    sessions.set(session);
  }

}
