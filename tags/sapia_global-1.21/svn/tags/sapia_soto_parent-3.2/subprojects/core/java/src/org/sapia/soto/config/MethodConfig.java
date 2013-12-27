/*
 * MethodTag.java
 *
 * Created on July 27, 2005, 1:37 PM
 *
 */

package org.sapia.soto.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author yduchesne
 */
public class MethodConfig {
  
  private String _name;
  private List _sig    = new ArrayList();
  private List _params = new ArrayList();
  
  /** Creates a new instance of MethodTag */
  public MethodConfig() {
  }
  
  public void setName(String name){
    _name = name;
  }
  
  public void addParamType(String className) throws Exception{
    _sig.add(Class.forName(className));
  }
  
  public void addParam(Object param){
    _params.add(param);
  }
  
  public void invoke(Object target) throws
    NoSuchMethodException, 
    IllegalAccessException, 
    InvocationTargetException{
    
    if(_name == null){
      throw new IllegalStateException("Method name not set");
    }
    
    Class[]  paramTypes = (Class[])_sig.toArray(new Class[_sig.size()]);
    Object[] params = (Object[])_params.toArray(new Object[_params.size()]);
    Method m = target.getClass().getMethod(_name, paramTypes);
    m.invoke(target, params);
  }
  
}
