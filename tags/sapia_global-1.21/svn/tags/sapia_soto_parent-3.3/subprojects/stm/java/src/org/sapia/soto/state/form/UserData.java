/*
 * SessionStack.java
 *
 * Created on September 2, 2005, 5:14 PM
 *
 */

package org.sapia.soto.state.form;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.StateExecException;
import org.sapia.soto.state.StatePath;

/**
 * An instance of this class is meant to keep user state.
 *
 * @author yduchesne
 */
public class UserData {
  
  private CallStack _calls = new CallStack(new IdFactory());
  private Map _data = new HashMap();
  private Locale _locale = Locale.ENGLISH;
  
  /** Creates a new instance of SessionStack */
  public UserData() {
  }
  
  /**
   * @param loc the <code>Locale</code> to assign to this user.
   */
  public void setLocale(Locale loc){
    _locale = loc;
  }
  
  /**
   * @return this instance's <code>Locale</code>.
   */
  public Locale getLocale(){
    return _locale;
  }
  
  public void performGoto(Result result, StatePath targetState, StatePath returnState) throws StateExecException{
    Call call = _calls.create();
    call.setReturn(returnState);
    result.exec(targetState);
  }
  
  public void performGoto(Result result, StatePath targetState) throws StateExecException{
    _calls.create();
    result.exec(targetState);
  }  
  
  public void performReturn(Result result) throws StateExecException{
    Call call = _calls.pop();
    if(call == null){
      throw new IllegalStateException("Invalid call stack; no state to return to");
    }
    call.performReturn(result);
  }  
  
  public void performReturn(Result result, StatePath returnState) throws StateExecException{
    Call call = _calls.pop();
    if(call == null){
      throw new IllegalStateException("Invalid call stack; no state to return to");
    }
    call.performReturn(result, returnState);
  }    
  
  /** 
   * Creates a form, internally pushes it on the form stack, and returns it.
   *
   * @return a <code>Form</code>.
   */
  public Form createForm(){
    Call call = _calls.peek();
    if(call == null){
      call = _calls.create();
    }
    return call.getForms().create();
  }  
  
  /** 
   * Internally pops the current form from the internal stack and returns
   * it.
   *
   * @return a <code>Form</code>, or <code>null</code> if no form currently
   * exists.
   */
  public Form clearForm(){
    Call call = _calls.peek();
    if(call == null){
      return null;
    }
    return call.getForms().pop();
  }    
  
  /** 
   * Internally pops the current form from the internal stack and returns
   * it.
   *
   * @param formId the ID of the expected form.
   * @return a <code>Form</code>, or <code>null</code> if no form currently
   * exists.
   * @throws IllegalFormException if the current form does not have an ID 
   * that matches the one passed in.
   */
  public Form clearForm(int formId) throws IllegalFormException{
    Call call = _calls.peek();
    if(call == null){
      return null;
    }
    Form form = call.getForms().pop();
    if(form == null) return null;
    if(form.getId() != formId){
      throw new IllegalFormException(""+formId);
    }
    return form;
  }      

  /**
   * @see #clearForm(int)
   */
  public Form clearForm(String formId) throws IllegalFormException{  
    return clearForm(Integer.parseInt(formId));
  }
  
  /**
   * This method returns the current form, check that its 
   * identifier matches the given one.
   * 
   * @param formId the ID of the expected form.
   * @return the current <code>Form</code>, or <code>null</code> if no form currently
   * exists.
   * @throws IllegalFormException if the current form does not have an ID 
   * that matches the one passed in.
   */
  public Form currentForm(int formId) throws IllegalFormException{
    Call call = _calls.peek();
    if(call == null){
      return null;
    }
    Form form = call.getForms().peek();
    if(form == null) return null;
    if(form.getId() != formId){
      throw new IllegalFormException(""+formId);
    }
    return form;
  }
  
  /** 
   * @return the current <code>Form</code>.
   * @see #currentForm(int)
   */  
  public Form currentForm(String formId){
    return currentForm(Integer.parseInt(formId));
  }  
  
  /**
   * @return the current <code>Form</code>, or <code>null</code> if no form currently
   * exists.
   */
  public Form currentForm() {
    Call call = _calls.peek();
    if(call == null){
      return null;
    }
    return call.getForms().peek();
  }

  /**
   * @see #currentForm()
   */
  public Form getCurrentForm(){
    return currentForm();
  }
  
  /**
   * @see #currentForm(int)
   */
  public Form getCurrentForm(int formId){
    return currentForm(formId);
  }  
  
  /**
   * @see #currentForm(String)
   */
  public Form getCurrentForm(String id){
    return currentForm(id);
  }  
  
  /**
   * @param key the key under which to bind the given object.
   * @param val an <code>Object</code>.
   */
  public void put(String key, Object val){
    _data.put(key, val);
  }
  
  /**
   * @param key the key for which the corresponding object should be returned.
   * @return the <code>Object</code> whose key corresponds to the given
   * one, or <code>null</code> if no such object exists.
   */
  public Object get(String key){
    return _data.get(key);
  }
  
  /**
   * Clears this instance's call stacks and forms.
   */
  public void clear(){
    _calls.clear();
  }
}
