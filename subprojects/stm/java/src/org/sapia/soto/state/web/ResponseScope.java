package org.sapia.soto.state.web;

import javax.servlet.http.HttpServletResponse;
import org.sapia.soto.state.Scope;

/**
 * Implements the <code>Scope</code> interface over an 
 * <code>HttpServletResponse</code>.
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
public class ResponseScope implements Scope {
  private HttpServletResponse _resp;

  public ResponseScope(HttpServletResponse resp) {
    _resp = resp;
  }

  /**
   * @see org.sapia.soto.state.Scope#getVal(java.lang.Object)
   */
  public Object getVal(Object key) {
    return null;
  }

  /**
   * @see org.sapia.soto.state.Scope#putVal(java.lang.Object, java.lang.Object)
   */
  public void putVal(Object key, Object value) {
    _resp.setHeader(key.toString(), value.toString());
  }

  /**
   * @return the <code>HttpServletResponse</code> that this instance wraps.
   */
  public HttpServletResponse getResponse() {
    return _resp;
  }
}
