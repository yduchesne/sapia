package org.sapia.regis.remote;

import org.sapia.regis.RegisSession;

public class RemoteSessions {
  
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
  
  public static boolean isJoined(){
    return sessions.get() != null;
  }
  
  public static void close(){
    if(sessions.get() != null){
      ((RegisSession)sessions.get()).close();      
      sessions.set(null);
    }
  }
}
