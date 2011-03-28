package org.sapia.soto.state.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.sapia.soto.state.Scope;

/**
 * Implements the <code>Scope</code> interface over an
 * <code>HttpSession</code>.
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
  private HttpServletRequest _req;

  public SessionScope(HttpServletRequest req) {
    _req = req;
  }

  /**
   * @see org.sapia.soto.state.Scope#putVal(java.lang.Object, java.lang.Object)
   */
  public void putVal(Object key, Object value) {
    HttpSession sess = _req.getSession();
    if(sess != null){
       sess.setAttribute(key.toString(), value);
    }
  }

  /**
   * @see org.sapia.soto.state.Scope#getVal(java.lang.Object)
   */
  public Object getVal(Object key) {
    HttpSession sess = _req.getSession();
    if(sess == null) {
      return null;
    }

    return sess.getAttribute(key.toString());
  }

  /**
   * @return the <code>HttpSession</code> held within this instance, or
   *         <code>null</code> if no session was created.
   * 
   * @see #createSession()
   */
  public HttpSession getSession() {
    return _req.getSession(false);
  }

  /**
   * @return creates a <code>HttpSession</code> and returns it, or returns the
   *         already existing session, if this applies.
   */
  public HttpSession createSession() {
    return _req.getSession(true);
  }
 
}
