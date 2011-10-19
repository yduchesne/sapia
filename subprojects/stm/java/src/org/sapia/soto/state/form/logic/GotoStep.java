/*
 * GotoStep.java
 *
 * Created on September 2, 2005, 10:49 PM
 *
 */

package org.sapia.soto.state.form.logic;

import org.sapia.soto.state.Result;
import org.sapia.soto.state.StateExecException;
import org.sapia.soto.state.StatePath;

/**
 *
 * @author yduchesne
 */
public class GotoStep extends UserStep{
  
  private StatePath _target, _return;
  
  /** Creates a new instance of GotoStep */
  public GotoStep() {
  }
  
  public void setTarget(String targetState){
    _target = StatePath.parse(targetState);
  }
  
  public void setReturn(String returnState){
    _return = StatePath.parse(returnState);
  }  
  
  public void execute(Result res){
    if(_target == null)
      throw new IllegalStateException("Target state not set");
    try{
      if(_return != null)
        super.getUserData(res).performGoto(res, _target.copy(), _return.copy());
      else
        super.getUserData(res).performGoto(res, _target.copy());
    }catch(StateExecException e){
      res.error(e);
    }
  }
}
