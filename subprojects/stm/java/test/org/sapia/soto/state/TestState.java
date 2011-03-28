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
public class TestState implements State {
  public boolean exec;
  public boolean success;
  String         successId;
  String         errId;
  String         id;
  int execCount;

  public TestState(boolean success) {
    this.success = success;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setSuccess(String flowId) {
    successId = flowId;
  }

  public void setError(String flowId) {
    errId = flowId;
  }

  public void execute(Result st) {
    if(!success) {
      st.setNextStateId(errId);
    } else {
      st.setNextStateId(successId);
    }
    execCount++;
    exec = true;
  }
}
