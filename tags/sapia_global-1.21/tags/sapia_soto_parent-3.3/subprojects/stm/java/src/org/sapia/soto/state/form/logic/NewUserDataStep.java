/*
 * NewUserDataStep.java
 *
 * Created on September 2, 2005, 9:30 PM
 *
 */

package org.sapia.soto.state.form.logic;

import org.sapia.soto.state.AbstractStep;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.form.UserData;

/**
 *
 * @author yduchesne
 */
public class NewUserDataStep extends AbstractStep {
  
  private String _key, _scope;
  
  
  /** Creates a new instance of NewUserDataStep */
  public NewUserDataStep() {
  }
  
  public void setKey(String key){
    _key = key;
  }
  
  public void setScope(String scope){
    _scope = scope;
  }
  
  public void execute(Result res){
    if(_key == null){
      throw new IllegalStateException("Key not set");
    }
    if(_scope == null){
      throw new IllegalStateException("Scope not set");
    }    
    UserData user = new UserData();
    res.getContext().put(_key, user, _scope);
    res.getContext().push(user);
  }
  
}
