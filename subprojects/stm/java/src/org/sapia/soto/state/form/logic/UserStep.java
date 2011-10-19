/*
 * UserStep.java
 *
 * Created on September 2, 2005, 10:07 PM
 *
 */

package org.sapia.soto.state.form.logic;

import org.sapia.soto.state.AbstractStep;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.StmKey;
import org.sapia.soto.state.form.UserData;

/**
 *
 * @author yduchesne
 */
public abstract class UserStep extends AbstractStep{
  
  private StmKey _userKey;
  
  /** Creates a new instance of UserStep */
  public UserStep() {
  }
  
  public void setUser(String key){
    _userKey = StmKey.parse(key);
  }
  
  protected UserData getUserData(Result res){
    if(_userKey == null){
      throw new IllegalStateException("User key not set");
    }
    UserData data = (UserData)_userKey.lookup(res);
    if(data == null){
      throw new IllegalArgumentException("User data not found under: " + _userKey);
    }
    return data;
     
  }
}
