/*
 * NewFormStep.java
 *
 * Created on September 2, 2005, 9:36 PM
 *
 */

package org.sapia.soto.state.form.logic;

import org.sapia.soto.state.AbstractStep;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.form.Form;


/**
 *
 * @author yduchesne
 */
public class NewFormStep extends AbstractStep{
  
  private String _key, _scope;
  
  /** Creates a new instance of NewFormStep */
  public NewFormStep() {
  }
  
  public void setScope(String scope){
    _scope = scope;
  }
  
  public void setKey(String key){
    _key = key;
  }
  
  public void execute(Result res){
    Form form = new Form();
    if(_key == null){
      res.getContext().push(form);
    }
    else{
      if(_scope == null){
        throw new IllegalStateException("Scope not set");
      }
      res.getContext().put(_key, form, _scope);
    }
  }
}
