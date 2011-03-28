/*
 * Call.java
 *
 * Created on September 2, 2005, 5:39 PM
 *
 */

package org.sapia.soto.state.form;

import org.sapia.soto.state.Result;
import org.sapia.soto.state.StateExecException;
import org.sapia.soto.state.StatePath;

/**
 *
 * @author yduchesne
 */
public class Call {
  
  private int _id;
  private StatePath _return;
  private FormStack _forms;
  
  /** Creates a new instance of Call */
  public Call(IdFactory fac) {
    _id = fac.id();
    _forms = new FormStack(fac);
  }
  
  public int getId(){
    return _id;
  }
  
  public void setReturn(StatePath returnState){
    _return = returnState;
  }
  
  public void performReturn(Result res) throws StateExecException{
    if(_return == null)
      throw new IllegalStateException("Return state not set");
    res.exec(_return.copy());
  }
  
  public void performReturn(Result res, StatePath returnState) throws StateExecException{
    res.exec(returnState);
  }  
  
  FormStack getForms(){
    return _forms;
  }
  

  
}
