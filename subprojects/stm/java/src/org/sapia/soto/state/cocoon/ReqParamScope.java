package org.sapia.soto.state.cocoon;

import org.apache.cocoon.environment.Request;
import org.sapia.soto.state.Scope;

/**
 * Implements the <code>Scope</code> interface over request parameters.
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
public class ReqParamScope implements Scope {
  private Request _req;

  public ReqParamScope(Request req) {
    _req = req;
  }

  /**
   * @see org.sapia.soto.state.Scope#putVal(java.lang.Object, java.lang.Object)
   */
  public void putVal(Object key, Object value) {
  }

  /**
   * @see org.sapia.soto.state.Scope#getVal(java.lang.Object)
   */
  public Object getVal(Object key) {
    return _req.getParameter(key.toString());
  }

  /**
   * @return the <code>Request</code> that this instance wraps.
   */
  public Request getRequest() {
    return _req;
  }
}
