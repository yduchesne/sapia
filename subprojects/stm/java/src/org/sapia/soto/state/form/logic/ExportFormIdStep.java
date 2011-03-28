/*
 * ExportFormId.java
 *
 * Created on September 6, 2005, 11:05 AM
 *
 */

package org.sapia.soto.state.form.logic;

import org.sapia.soto.state.Result;
import org.sapia.soto.state.StmKey;
import org.sapia.soto.state.form.Form;
import org.sapia.soto.state.form.UserData;

/**
 *
 * @author yduchesne
 */
public class ExportFormIdStep extends UserStep{
  
  private StmKey _key;
  
  /** Creates a new instance of ExportFormId */
  public ExportFormIdStep() {
  }
  
  public void setTo(String key){
    _key = StmKey.parse(key);
  }
  
  public void execute(Result res){
    if(_key == null){
      throw new IllegalStateException("To not set");
    }
    UserData data = super.getUserData(res);
    if(data == null){
      res.error("No user data under: " + _key);
      return;
    }
    Form form = data.currentForm();
    if(form != null && _key.scopes != null){
      for(int i = 0; i < _key.scopes.length; i++){
        String id = Integer.toString(form.getId());
        res.getContext().put(_key.key, id, _key.scopes[i]);
      }
    }
  }
  
  
}
