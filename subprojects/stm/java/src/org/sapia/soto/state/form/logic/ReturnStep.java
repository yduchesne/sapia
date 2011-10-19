/*
 * ReturnStep.java
 *
 * Created on September 2, 2005, 10:53 PM
 *
 */

package org.sapia.soto.state.form.logic;

import org.sapia.soto.state.Result;
import org.sapia.soto.state.StateExecException;
import org.sapia.soto.state.StatePath;
import org.sapia.soto.state.form.UserData;

/**
 *
 * @author yduchesne
 */
public class ReturnStep extends UserStep{
  
  private StatePath _return;
  
  /** Creates a new instance of ReturnStep */
  public ReturnStep() {
  }
  
  public void setReturn(String returnState){
    _return = StatePath.parse(returnState);
  }
  
  public void execute(Result res){
    UserData user = getUserData(res);
    try{
      if(_return != null)
        user.performReturn(res, _return);
      else
        user.performReturn(res);
    }catch(StateExecException e){
      res.error(e);
    }    
  }
  
}
