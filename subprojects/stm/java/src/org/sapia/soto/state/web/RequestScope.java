package org.sapia.soto.state.web;

import javax.servlet.http.HttpServletRequest;
import org.sapia.soto.state.Scope;

/**
 * Implements the <code>Scope</code> interface over an 
 * <code>HttpServletRequest</code>.
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
public class RequestScope implements Scope {
  public static final String CONTEXT_PATH_KEY = "contextPath";
  private HttpServletRequest _req;

  /**
   * Constructs an instance of this class that wraps the given request.
   * 
   * @param req
   *          a <code>Request</code>.
   */
  public RequestScope(HttpServletRequest req) {
    _req = req;
    _req.setAttribute(CONTEXT_PATH_KEY, req.getContextPath());
  }

  /**
   * @see org.sapia.soto.state.Scope#getVal(java.lang.Object)
   */
  public Object getVal(Object key) {
    return _req.getAttribute(key.toString());
  }

  /**
   * @see org.sapia.soto.state.Scope#putVal(java.lang.Object, java.lang.Object)
   */
  public void putVal(Object key, Object value) {
    _req.setAttribute(key.toString(), value);
  }

  /**
   * @return the <code>Request</code> wrapped by this instance.
   */
  public HttpServletRequest getRequest() {
    return _req;
  }
}
