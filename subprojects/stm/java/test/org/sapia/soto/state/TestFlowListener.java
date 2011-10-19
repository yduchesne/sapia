package org.sapia.soto.state;

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
public class TestFlowListener implements StateExecListener {
  boolean pre;
  boolean post;
  boolean err;

  /**
   *  
   */
  public TestFlowListener() {
    super();
  }

  public void onError(Result st, String flowId, Err err) {
    this.err = true;
  }

  public void onPostExec(Result st, String flowId) {
    post = true;
  }

  public void onPreExec(Result st, String flowId) {
    pre = true;
  }
}
