package org.sapia.soto.state.config;

import org.apache.commons.lang.StringUtils;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.State;
import org.sapia.soto.state.StateExecException;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;

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
public class StateInterceptor implements State, ObjectHandlerIF {
  private String[] _preExec;
  private String[] _postExec;
  private State    _toExecute;

  public StateInterceptor() {
  }

  /**
   * @param list
   *          the comma delimited list of states that must be executed prior to
   *          the state that this instance wraps.
   */
  public void setPreExec(String list) {
    _preExec = StringUtils.split(list, ",");

    for(int i = 0; i < _preExec.length; i++) {
      _preExec[i] = _preExec[i].trim();
    }
  }

  /**
   * @param list
   *          the comma delimited list of states that must be executed after the
   *          state that this instance wraps.
   */
  public void setPostExec(String list) {
    _postExec = StringUtils.split(list, ",");

    for(int i = 0; i < _postExec.length; i++) {
      _postExec[i] = _postExec[i].trim();
    }
  }

  /**
   * @see State#getId()
   */
  public String getId() {
    return _toExecute.getId();
  }

  /**
   * @see org.sapia.soto.state.Executable#execute(org.sapia.soto.state.Result)
   */
  public void execute(Result st) {
    if(_toExecute == null) {
      throw new IllegalStateException(
          "State decorator does not contain any state");
    }

    if(_preExec != null) {
      for(int i = 0; i < _preExec.length; i++) {
        try {
          st.exec(_preExec[i], null);
        } catch(StateExecException e) {
          st.error(e);
        }
        if(st.isError() || st.isAborted()) {
          return;
        }
      }
    }

    _toExecute.execute(st);

    if(st.isError() || st.isAborted()) {
      return;
    }

    if(_postExec != null) {
      for(int i = 0; i < _postExec.length; i++) {
        try {
          st.exec(_postExec[i], null);
        } catch(StateExecException e) {
          st.error(e);
        }

        if(st.isError() || st.isAborted()) {
          return;
        }
      }
    }
  }

  public void setState(State state) {
    _toExecute = state;
  }

  public void addPreExec(String stateId) {
    _preExec = grow(_preExec, stateId);
  }

  public void addPostExec(String stateId) {
    _postExec = grow(_postExec, stateId);
  }

  static String[] grow(String[] in, String s) {
    if(in == null) {
      in = new String[0];
    }

    String[] newArr = new String[in.length + 1];
    System.arraycopy(in, 0, newArr, 0, in.length);

    return newArr;
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(java.lang.String,
   *      java.lang.Object)
   */
  public void handleObject(String name, Object obj)
      throws ConfigurationException {
    if(obj instanceof State) {
      _toExecute = (State) obj;
    }
  }
}
