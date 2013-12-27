/*
 * ClearStep.java
 *
 * Created on September 11, 2005, 8:25 PM
 *
 */

package org.sapia.soto.state.form.logic;

import org.sapia.soto.state.Result;
import org.sapia.soto.state.form.UserData;

/**
 *
 * @author yduchesne
 */
public class ClearStep extends UserStep{
  
  /** Creates a new instance of ClearStep */
  public ClearStep() {
  }
  
  public void execute(Result res){
    UserData data = getUserData(res);
    data.clear();
  }
  
}
