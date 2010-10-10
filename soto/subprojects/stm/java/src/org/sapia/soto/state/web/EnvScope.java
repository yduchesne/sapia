package org.sapia.soto.state.web;

import org.sapia.soto.Env;
import org.sapia.soto.NotFoundException;
import org.sapia.soto.state.Scope;

/**
 * Implements the <code>Scope</code> interface on top of an <code>Env</code>
 * instance.
 * 
 * @see org.sapia.soto.Env
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
public class EnvScope implements Scope {
  private Env _env;

  public EnvScope(Env env) {
    _env = env;
  }

  /**
   * @see org.sapia.soto.state.Scope#getVal(java.lang.Object)
   */
  public Object getVal(Object key) {
    try {
      return _env.lookup(key.toString());
    } catch(NotFoundException e) {
      return null;
    }
  }

  /**
   * @see org.sapia.soto.state.Scope#putVal(java.lang.Object, java.lang.Object)
   */
  public void putVal(Object key, Object value) {
  }
}
