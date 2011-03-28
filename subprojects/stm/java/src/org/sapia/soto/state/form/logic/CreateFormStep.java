/*
 * NewFormStep.java
 *
 * Created on September 2, 2005, 9:36 PM
 *
 */

package org.sapia.soto.state.form.logic;

import org.sapia.soto.state.Result;
import org.sapia.soto.state.form.Form;
import org.sapia.soto.state.form.UserData;

/**
 *
 * @author yduchesne
 */
public class CreateFormStep extends UserStep{
  
  /** Creates a new instance of NewFormStep */
  public CreateFormStep() {
  }
  
  public void execute(Result res){
    UserData data = getUserData(res);
    Form form = data.createForm();
    res.getContext().push(form);
  }
}
