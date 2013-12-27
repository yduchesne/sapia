/*
 * FormStack.java
 *
 * Created on September 2, 2005, 5:19 PM
 *
 */

package org.sapia.soto.state.form;

import java.util.Stack;

/**
 *
 * @author yduchesne
 */
public class FormStack {
  
  private Stack _forms = new Stack();
  private IdFactory _fac;
  
  /** Creates a new instance of FormStack */
  public FormStack(IdFactory fac) {
    _fac = fac;
  }
  
  public synchronized Form retrieve(int formId){
    for(int i = 0; i < _forms.size(); i++){
      Form f = (Form)_forms.get(i);
      if(f.getId() == formId){
        return f;
      }
    }
    return null;
  }
  
  public synchronized Form peek(){
    if(_forms.size() == 0)
      return null;
    return (Form)_forms.peek();
  }
  
  public synchronized Form pop(){
    if(_forms.size() == 0)
      return null;
    return (Form)_forms.pop();
  }  
  
  public synchronized Form create(){
    Form f = new Form(_fac.id());
    _forms.push(f);
    return f;
  }
  
  public synchronized void cancel(int formId){
    if(_forms.size() == 0) return;
    int result = -1;
    for(int i = 0; i < _forms.size(); i++){
      Form f = (Form)_forms.get(i);
      if(f.getId() == formId){
        result = i;
        break;
      }
    }
    if(result > 0){
      while(result < _forms.size()){
        _forms.removeElementAt(result);
      }
    }
  }
  
  public String toString(){
    return new StringBuffer("[")
      .append("forms=").append(_forms)
      .append("]").toString();
  }
}
