package org.sapia.soto.state.config;

import org.sapia.soto.state.Result;
import org.sapia.soto.state.State;

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
public class TestState implements State {

  boolean success, exec;
  String  id;

  public TestState(boolean success) {
    this.success = success;
  }

  /**
   * @see org.sapia.soto.state.State#getId()
   */
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  /**
   * @see org.sapia.soto.state.Executable#execute(org.sapia.soto.state.Result)
   */
  public void execute(Result st) {
    exec = true;
    if(!success) {
      st.error("test");
    }
  }

}
