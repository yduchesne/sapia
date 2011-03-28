package org.sapia.soto.state;

import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;

/**
 * Implements the <code>State</code> interface over the
 * <code>ExecContainer</code> class. An instance of this class holds execution
 * <code>Step</code> s that are executed sequentially when the instance's
 * executed() method is called.
 * 
 * @see org.sapia.soto.state.Step
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
public class StepState extends ExecContainer implements State, ObjectHandlerIF {
  private StatePath _errState;
  private StatePath _successState;
  private String _id;

  public StepState() {
    super();
  }

  /**
   * Sets the identifier of this instance.
   * 
   * @see org.sapia.soto.state.State#getId()
   */
  public void setId(String id) {
    _id = id;
  }

  /**
   * @see org.sapia.soto.state.State#getId()
   */
  public String getId() {
    return _id;
  }

  /**
   * @param errState
   *          the identifier of the state to execute if an error is signaled by
   *          one of the steps held by this instance.
   */
  public void setError(String errState) {
    _errState = StatePath.parse(errState);
  }

  /**
   * @param successState
   *          the identifier of the state to execute if no step held within this
   *          instance signals an error in the context of the execution.
   */
  public void setSuccess(String successState) {
    _successState = StatePath.parse(successState);
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(java.lang.String,
   *      java.lang.Object)
   */
  public void handleObject(String name, Object obj)
      throws ConfigurationException {
    if(obj instanceof Step) {
      _execs.add(obj);
    }
    else{
      throw new ConfigurationException("Expected " + Step.class.getName() + 
        " instance; got " + obj.getClass().getName() + " for :" + name);
    }
  }

  /**
   * @see org.sapia.soto.state.ExecContainer#handleError(Result)
   */
  protected void handleError(Result res) {
    if(_errState != null) {
      try {
        res.exec(_errState.copy());
      } catch(StateExecException e) {
        res.error(e);

        return;
      }
    }
  }

  /**
   * @see org.sapia.soto.state.ExecContainer#handleSuccess(Result)
   */
  protected void handleSuccess(Result res) {
    if(_successState != null) {
      try {
        res.exec(_successState.copy());
      } catch(StateExecException e) {
        res.error(e);
        return;
      }
    }
  }
}
