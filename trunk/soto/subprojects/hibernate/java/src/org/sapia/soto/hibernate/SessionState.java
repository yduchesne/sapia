package org.sapia.soto.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * This class internally uses a <code>ThreadLocal</code> instance to register
 * a Hibernate session with the current thread.
 * <p>
 * Usage:
 * <pre>
 *  // the hibernate variable is an instance of HibernateService...
 *  SessionFactory factory = hibernate.getSessionFactory();
 *  Session session = factory.openSession();
 *  Session session = SessionState.join(session);
 *  
 *  // Later on in application code
 *  Session s = SessionState.currentSession(); 
 *  Transaction tx = null; 
 *  try{
 *    tx = s.beginTransaction();
 *
 *    // here call application code
 *
 *    tx.commit();
 *
 *  }catch(Exception e){
 *    if(tx != null)
 *      tx.rollback();
 *    throw e;
 *  }finally{
 *    // the following closes the session and "deregisters" it from the 
 *    // current thread..
 *    SessionState.unjoinAndClose();
 *
 *    // note that SessionState.unregister(false) would deregister the session
 *    // from the current thread but would not close it.
 *  }
 * </pre>
 * <p>
 * The above usage decouples application code from session management, and thus
 * makes that application code reusable independantly of trivial session
 * issues (such as opening, flushing, etc.). In order to access the current 
 * session from application code, one proceeds as follows:
 * 
 * <pre>
 *  Sessions session = SessionState.currentSession();
 *  // perform work using session...
 * </pre> 
 *
 * @see HibernateService
 *
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2003 <a
 *         href="http://www.sapia-oss.org">Sapia Open Source Software </a>. All
 *         Rights Reserved.</dd>
 *         </dt>
 *         <dt><b>License: </b>
 *         <dd>Read the license.txt file of the jar or visit the <a
 *         href="http://www.sapia-oss.org/license.html">license page </a> at the
 *         Sapia OSS web site</dd>
 *         </dt>
 *         </dl>
 */
public class SessionState {
  static ThreadLocal _sessionRef = new ThreadLocal();
  
  private SessionState() {}

  /**
   * Registers the calling thread with a session that is internally created
   * using the passed in factory.
   * 
   * @param factory
   *          a <code>SessionFactory</code>.
   * @return a <code>Session</code> that was create with the passed in factory.
   * @throws IllegalStateException
   *           if the current thread is already registered with a session.
   */
  public static Session register(SessionFactory factory) throws IllegalStateException {
    if(_sessionRef.get() != null) {
      throw new IllegalStateException(
          "Thread already registered with a session");
    }

    Session s = factory.openSession();
    _sessionRef.set(s);
    return s;
  }
  
  /**
   * Registers the calling thread with the given session. If the calling
   * thread is already registered with a session, that registratrion no longer
   * holds and the thread will be registered with the passed in session from
   * now on.
   *
   * @param session a <code>Session</code>
   */
  public static void join(Session session){
    _sessionRef.set(session);    
    if(!session.isConnected())session.reconnect();
  }

  /**
   * Unregisters the session that is associated to the calling thread.
   *
   * @param close if <code>true</code>, this method internally closes the
   * session.
   * 
   * @deprecated use one of the unjoin() methods.
   * 
   * @see #unjoin()
   * @see #unjoinAndClose()
   */
  public static void unregister(boolean close) {
    Session s = (Session)_sessionRef.get();
    if(s != null){
      if(close){
        if(s.isOpen()){
          s.close();
        }
      }
      else{
        if(s.isConnected()){
          s.disconnect();
        }
      }
      _sessionRef.set(null);      
    }
  }
  
  /**
   * Unjoins the calling thread from the session to which it is currently joined.
   */
  public static void unjoin(){
    _sessionRef.set(null);
  }
  
  /**
   * Unjoins the calling thread from the session to which it is currently joined,
   * and closes that session.
   */
  public static void unjoinAndClose(){
    Session s = (Session)_sessionRef.get();
    if(s != null){
      s.close();
    }
  }  

  /**
   * Returns the session that is registered with the calling thread.
   * 
   * @return a <code>Session</code>.
   * @throws IllegalStateException
   *           if no session is registered with the calling thread.
   */
  public static Session currentSession() throws IllegalStateException {
    Session s;

    if((s = (Session) _sessionRef.get()) == null) {
      throw new IllegalStateException("Thread not registered with a session");
    }
    
    if(!s.isConnected()){
      s.reconnect();
    }

    return s;
  }
  
  /**
   * Returns the current session, or internally creates one if none is 
   * registered with the current thread - that new session is registered
   * with the current thread prior to being returned.
   *
   * @param the <code>SessionFactory</code> to use to create a session - if
   * this applies.
   * @return a <code>Session</code>.
   */
  public static Session createSession(SessionFactory factory){
    Session s;

    if((s = (Session) _sessionRef.get()) == null) {
      s = factory.openSession();
      _sessionRef.set(s);
    }
    else{
      if(!s.isConnected())s.reconnect();
    }

    return s;    
  }

  /**
   * Returns the session that is registered with the calling thread. If none is
   * currently registered, this method uses the given session factory to create
   * one; it registers the created session with the calling thread and returns
   * it.
   * 
   * @param fac
   *          a <code>SessionFactory</code>.
   * @return a <code>Session</code>.
   * @throws HibernateException
   */
  public static Session currentSession(SessionFactory fac)
      throws HibernateException {
    Session s;

    if((s = (Session) _sessionRef.get()) == null) {
      s = fac.openSession();
      _sessionRef.set(s);
    }
    else{
      if(!s.isConnected())s.reconnect();
    }

    return s;
  }

  /**
   * @return <code>true</code> if the calling thread is registered with a
   *         session.
   * @deprecated use {@link #isJoined()}        
   */
  public static boolean isRegistered() {
    return isJoined();
  }
  
  /**
   * @return <code>true</code> if the calling thread is joined to a
   *         Hibernate session.
   */  
  public static boolean isJoined(){
    return _sessionRef.get() != null;
  }
  
  /**
   * Calls the <code>reconnect()</code> method on the given session
   * and registers it with the calling thread.
   *
   * @param session a <code>Session</code>.
   */
  public static void reconnect(Session session){
    Session current = (Session)_sessionRef.get();
    if(current != null){
      if(current.equals(session)){
        if(!current.isConnected())current.reconnect();
      }
      else{
        throw new IllegalStateException("Thread already registered with " +
          "another session; call unregister() or disconnect() prior to " +
          "calling this method");
      }
    }
    else{
      if(!session.isConnected()){
        session.reconnect();
        _sessionRef.set(session);
      }
      else{
        _sessionRef.set(session);
      }
    }
  }
  
  /**
   * Calls the <code>disconnect()</code> method on the session that is 
   * currently registered with the calling thread - also unregisters that
   * session from it.
   */
  public static void disconnect(){
    Session session = (Session)_sessionRef.get();
    if(session != null && session.isConnected()){
      session.disconnect();
      _sessionRef.set(null);
    }
  }
}
