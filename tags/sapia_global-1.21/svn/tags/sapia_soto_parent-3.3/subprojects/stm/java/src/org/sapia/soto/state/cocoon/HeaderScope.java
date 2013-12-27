package org.sapia.soto.state.cocoon;

import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Response;
import org.sapia.soto.state.Scope;

/**
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class HeaderScope implements Scope {

  private Request  _req;
  private Response _res;

  public HeaderScope(Request req, Response res) {
    _res = res;
    _req = req;
  }

  /**
   * @see org.sapia.soto.state.Scope#getVal(java.lang.Object)
   */
  public Object getVal(Object key) {
    return _req.getHeader(key.toString());
  }

  /**
   * @see org.sapia.soto.state.Scope#putVal(java.lang.Object, java.lang.Object)
   */
  public void putVal(Object key, Object value) {
    _res.setHeader(key.toString(), value != null ? value.toString() : null);
  }

}
