package org.sapia.soto.state.config;

import java.util.List;

import org.sapia.soto.Debug;
import org.sapia.soto.state.ExecContainer;
import org.sapia.soto.state.Executable;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.State;

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
public class GlobalWrapper implements State {
  private ExecContainer _pre  = new ExecContainer();
  private ExecContainer _post = new ExecContainer();
  private State         _toExec;

  /**
   *  
   */
  GlobalWrapper(State st) {
    _toExec = st;
  }

  /**
   * @see org.sapia.soto.state.State#getId()
   */
  public String getId() {
    return _toExec.getId();
  }

  /**
   * @see org.sapia.soto.state.Executable#execute(org.sapia.soto.state.Result)
   */
  public void execute(Result st) {
    _pre.execute(st);

    if(st.isError()) {
      if(Debug.DEBUG) {
        Debug.debug("Not executing: error");
      }

      return;
    } else if(st.isAborted()) {
      if(Debug.DEBUG) {
        Debug.debug("Not executing: aborted");
      }

      return;
    }

    if(Debug.DEBUG) {
      Debug.debug("Executing: " + _toExec.getId() + "("
          + _toExec.getClass().getName() + ")");
    }

    _toExec.execute(st);
    _post.execute(st);
  }

  State getState() {
    return _toExec;
  }

  void addPre(List pre) {
    addExecs(_pre, pre);
  }

  void addPost(List post) {
    addExecs(_post, post);
  }

  private static void addExecs(ExecContainer cont, List execs) {
    for(int i = 0; i < execs.size(); i++) {
      cont.addExecutable((Executable) execs.get(i));
    }
  }
}
