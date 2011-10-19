package org.sapia.soto.state.cocoon;

import org.apache.cocoon.environment.http.HttpRequest;
import org.apache.commons.lang.ClassUtils;
import org.sapia.soto.Debug;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.helpers.CompositeStep;

/**
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
public class IfNotSession extends CompositeStep {
  public IfNotSession() {
  }

  /**
   * @see org.sapia.soto.state.Step#getName()
   */
  public String getName() {
    return ClassUtils.getShortClassName(getClass());
  }

  /**
   * @see org.sapia.soto.state.helpers.CompositeStep#doExecute(org.sapia.soto.state.Result)
   */
  protected boolean doExecute(Result res) {
    HttpRequest req = (HttpRequest) ((CocoonContext) res.getContext())
        .getRequest();
    boolean exists = req.getSession(false) != null;

    if(Debug.DEBUG) {
      Debug.debug("Session exists: " + exists);
    }

    return !exists;
  }
}
