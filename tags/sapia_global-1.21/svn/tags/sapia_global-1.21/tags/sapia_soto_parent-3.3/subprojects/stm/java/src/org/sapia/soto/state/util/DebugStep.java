package org.sapia.soto.state.util;

import org.sapia.soto.Debug;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.Step;

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
public class DebugStep implements Step {
  private String _msg;

  public DebugStep() {
  }

  /**
   * @see org.sapia.soto.state.Step#getName()
   */
  public String getName() {
    return "Echo";
  }

  public void setMsg(String msg) {
    _msg = msg;
  }

  /**
   * @see org.sapia.soto.state.Executable#execute(org.sapia.soto.state.Result)
   */
  public void execute(Result st) {
    if((_msg != null) && Debug.DEBUG) {
      Debug.debug(_msg);
    }
  }
}
