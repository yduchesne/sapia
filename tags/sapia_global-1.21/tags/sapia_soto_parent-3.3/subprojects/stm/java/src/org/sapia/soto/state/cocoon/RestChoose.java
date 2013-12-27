/*
 * RestStep.java
 *
 * Created on April 6, 2005, 8:56 PM
 */

package org.sapia.soto.state.cocoon;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.State;
import org.sapia.soto.state.Step;
import org.sapia.soto.state.StepState;
import org.sapia.soto.state.config.StepContainer;

/**
 *
 * @author yduchesne
 */
public class RestChoose extends StepState implements Step, State{
  
  public static final String GET    = "GET";
  public static final String POST   = "POST";  
  public static final String PUT    = "PUT";    
  public static final String DELETE = "DELETE";    
  
  private List _methods = new ArrayList(4);
  
  /** Creates a new instance of RestStep */
  public RestChoose() {
  }
  
  public MethodSwitch createGet(){
    return create(GET);
  }
  
  public MethodSwitch createPut(){
    return create(PUT);
  }  
  
  public MethodSwitch createPost(){
    return create(POST);
  }  
  
  public MethodSwitch createDelete(){
    return create(DELETE);
  }    
  
  public String getName(){
    return ClassUtils.getShortClassName(getClass());
  }
  
  public void execute(Result result){
    for(int i = 0; i < _methods.size(); i++){
      MethodSwitch ms = (MethodSwitch)_methods.get(i);
      if(ms.matches(result)){
        ms.execute(result);
        return;
      }
    }
    result.error("Cannot handle HTTP request method: " + 
      ((CocoonContext)result.getContext()).getRequest().getMethod());
  }
  
  private MethodSwitch create(String method){
    MethodSwitch ms = new MethodSwitch(method);
    _methods.add(ms);
    return ms;
  }
  
  public static class MethodSwitch extends StepContainer{
    
    private String _method;
    
    public MethodSwitch(String method){
      _method = method;
    }
    
    public boolean matches(Result res){
      String method = ((CocoonContext)res.getContext()).getRequest().getMethod();
      if(method == null){
        throw new IllegalStateException("HTTP request method not specified");
      }
      return _method.equalsIgnoreCase(method);
    }
    
  }
  
}
