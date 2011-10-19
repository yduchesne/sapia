/*
 * PushForm.java
 *
 * Created on September 2, 2005, 9:41 PM
 *
 */

package org.sapia.soto.state.form.logic;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.StmKey;
import org.sapia.soto.state.form.Form;
import org.sapia.soto.state.form.IllegalFormException;
import org.sapia.soto.state.form.UserData;

/**
 *
 * @author yduchesne
 */
public class PushFormStep extends UserStep{
  
  private StmKey _key;
  
  /** Creates a new instance of PushForm */
  public PushFormStep() {
  }
  
  public void setFormId(String key){
    _key = StmKey.parse(key);
  }
  
  public void execute(Result res){
    UserData user = getUserData(res);
    if(_key != null){
      String formId = (String)_key.lookup(res);
      if(formId != null){
        try{
          Form form = user.currentForm(formId);
          if(form == null){
            res.error(new IllegalFormException("No form found for identifier: " + _key));
            return;
          }
          res.getContext().push(form);
        }catch(IllegalFormException e){
          res.error(e);
        }
      }
      else{
        res.error(new IllegalFormException("Form identifier not found under: " + _key));
      }
    }
    else{
      Form form = user.currentForm();
      if(form == null){
        res.error("No current form found");
        return;
      }
      res.getContext().push(form);
    }
  }
}
