package org.sapia.soto.state;

import org.apache.commons.lang.ClassUtils;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 * Models a state reference: refers to a previously configured state, with the
 * latter's identifier.
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
public class StateRef implements Step, ObjectCreationCallback {
  private String    _id;
  private String    _module;
  private StatePath _path;

  public StateRef() {
  }

  /**
   * @see org.sapia.soto.state.Step#getName()
   */
  public String getName() {
    return ClassUtils.getShortClassName(getClass());
  }

  /**
   * @param id
   *          the identifier of the flow to which this instance refers.
   */
  public void setId(String id) {
    _id = id;
  }

  /**
   * @see State#getId()
   */
  public String getId() {
    return _id;
  }

  /**
   * @param module
   *          the name of the module to which the state to execute belongs.
   */
  public void setModule(String module) {
    _module = module;
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    if(_module == null){
      _path = StatePath.parse(_id);
    }
    else{
      _path = new StatePath(_id, _module);
    }
    return this;
  }

  /**
   * @see org.sapia.soto.state.Executable#execute(Result)
   */
  public void execute(Result res) {
    if(_id == null) {
      throw new IllegalStateException("'id' not specified on stateRef");
    }

    String currentState = res.getCurrentStateId();

    try {
      res.exec(_path.copy());
    } catch(StateExecException e) {
      res.error(e);

      return;
    }

    res.setCurrentStateId(currentState);
  }
}
