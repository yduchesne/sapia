package org.sapia.soto.state;

import org.sapia.soto.Service;
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
public class StateMachineService implements Service, ObjectHandlerIF {
  protected StateMachine _stm = new StateMachine();

  /**
   *  
   */
  public StateMachineService() {
  }

  /**
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
    _stm.init();
  }

  /**
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {
  }

  /**
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
  }

  /**
   * Adds a state machine to this instance.
   * 
   * @param stm
   *          a <code>StateMachine</code>.
   */
  public void addStateMachine(StateMachine stm) throws ConfigurationException {
    _stm.merge(stm);
  }

  /**
   * @see StateMachine#execute(String, String, Context)
   */
  public Result execute(String stateId, String module, Context ctx)
      throws StateExecException {
    return _stm.execute(stateId, module, ctx);
  }
  
  /**
   * @see StateMachine#execute(StatePath, Context)
   */
  public Result execute(StatePath path, Context ctx)
      throws StateExecException {
    return _stm.execute(path, ctx);
  }  

  /**
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(java.lang.String,
   *      java.lang.Object)
   */
  public void handleObject(String name, Object obj)
      throws ConfigurationException {
    if(obj instanceof StateMachine) {
      addStateMachine((StateMachine) obj);
    }
  }
}
