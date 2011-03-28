package org.sapia.soto.state.cocoon;

import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.sapia.soto.state.Scope;

/**
 * Implements the <code>Scope</code> interface over a Cocoon
 * <code>Session</code>.
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
public class SessionScope implements Scope {
  private Request _req;

  public SessionScope(Request req) {
    _req = req;
  }

  /**
   * @see org.sapia.soto.state.Scope#putVal(java.lang.Object, java.lang.Object)
   */
  public void putVal(Object key, Object value) {
    Session sess = _req.getSession();
    if(sess != null){
       sess.setAttribute(key.toString(), value);
    }
  }

  /**
   * @see org.sapia.soto.state.Scope#getVal(java.lang.Object)
   */
  public Object getVal(Object key) {
    Session sess = _req.getSession();
    if(sess == null) {
      return null;
    }

    return sess.getAttribute(key.toString());
  }

  /**
   * @return the <code>Session</code> held within this instance, or
   *         <code>null</code> if no session was created.
   * 
   * @see #createSession()
   */
  public Session getSession() {
    return _req.getSession(false);
  }

  /**
   * @return creates a <code>Session</code> and returns it, or returns the
   *         already existing session, if this applies.
   */
  public Session createSession() {
    return _req.getSession(true);
  }
 
}
