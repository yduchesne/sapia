package org.sapia.regis;

public class SessionUtil {
  
  private static ThreadLocal _session = new ThreadLocal();

  /**
   * @param obj an <code>Object</code> implementing the <code>RegistryProvider</code>
   * interface.
   * 
   * @return the <code>RegisSession</code> that was internally created and associated with
   * the current thread.
   */
  public static RegisSession createSessionFor(Object obj){
    if(obj instanceof RegistryProvider){
      RegistryProvider provider = (RegistryProvider)obj;
      RegisSession session = provider.getRegistry().open();
      _session.set(session);
      return session;
    }
    else{
      throw new IllegalArgumentException("Object must be instance of " + 
          RegistryProvider.class.getName());
    }
  }
  
  /**
   * @return the <code>RegisSession</code> that is registered witht the current thread.
   */
  public static RegisSession current(){
    RegisSession session = (RegisSession)_session.get();
    if(session == null){
      throw new IllegalStateException("Thread not registered with registry session");
    }
    return session;
  }
  
  /**
   * This method "joins" the current thread to the given session .
   * @param session a <code>RegisSession</code> to associate with the current thread. If
   * the current thread is already associated session, no exception is thrown and the
   * given session becomes the associated session.
   */
  public static void join(RegisSession session){
    _session.set(session);
  }
  
  /**
   * This method "unjoins" the current thread from the given session.
   */
  public static void unjoin(){
    _session.set(null);
  }
  
  /**
   * This method closes the session with which the current thread is
   * associated and unjoins the current thread.
   * 
   * @see #unjoin()
   */
  public static void close(){
    if(isJoined()){
      current().close();
      unjoin();      
    }
  }
  
  /**
   * @return <code>true</code> if the current thread is joined with a session.
   * 
   * @see #join(RegisSession)
   */
  public static boolean isJoined(){
    return _session.get() != null; 
  }

}
