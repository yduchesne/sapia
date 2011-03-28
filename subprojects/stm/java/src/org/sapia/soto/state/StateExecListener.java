package org.sapia.soto.state;

/**
 * Specifies the behavior of listeners that are notified upon execution of
 * flows.
 * 
 * @see StateMachine#addStateExecListener(StateExecListener)
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
public interface StateExecListener {
  /**
   * Called before a flow is executed.
   * 
   * @param st
   *          the <code>State</code> corresponding to the execution.
   * @param flowId
   *          the ID of the executed flow.
   */
  public void onPreExec(Result st, String flowId);

  /**
   * Called after a flow is executed.
   * 
   * @param st
   *          the <code>State</code> corresponding to the execution.
   * @param flowId
   *          the ID of the executed flow.
   */
  public void onPostExec(Result st, String flowId);

  /**
   * Called after a flow's execution, if an error has occurred.
   * 
   * @param st
   *          the <code>State</code> corresponding to the execution.
   * @param flowId
   *          the ID of the executed flow.
   * @param err
   *          the <code>Err</code> object that encapsulates error data.
   * 
   * @see Result#isError()
   */
  public void onError(Result st, String flowId, Err err);
}
