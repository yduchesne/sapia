package org.sapia.regis.hibernate;

import org.hibernate.Session;

public class Sessions {
  
  private static ThreadLocal sessions = new ThreadLocal();
  
  public static Session get(){
    if(sessions.get() == null){
      throw new IllegalStateException("Calling thread not registered with session");
    }
    Session s = (Session)sessions.get();
    if(!s.isOpen()){
      s = s.getSessionFactory().openSession();
      sessions.set(s);
    }
    return s;
  }

  public static void join(Session session){
    sessions.set(session);
  }
  
  public static void close(){
    if(sessions.get() != null){
      Session s = (Session)sessions.get();
      try{
        s.flush();
      }catch(RuntimeException e){
        s.close();
        sessions.set(null);
        throw e;
      }
      s.close();
      sessions.set(null);
    }
  }
 

  public static boolean isRegistered(){
    return sessions.get() != null;
  }
}
