/*
 * JythonAdapter.java
 *
 * Created on December 1, 2005, 2:16 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.sapia.soto.jython;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyProxy;
import org.python.util.PythonInterpreter;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.NotFoundException;
import org.sapia.soto.jython.bean.Property;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;
import org.sapia.util.xml.confix.ObjectHandlerIF;

/**
 * @author yduchesne
 */
public class JythonAdapter implements EnvAware, ObjectHandlerIF, ObjectCreationCallback{
  
  private JythonService       _jython;
  private String              _pyClass, _pyModule;
  private Env                 _env;
  private Object              _pyObj;
  private Map                 _configProps = new HashMap();

  public JythonAdapter() {}

  /**
   * @see org.sapia.soto.EnvAware#setEnv(org.sapia.soto.Env)
   */
  public void setEnv(Env env) {
    try {
      _env = env;
      _jython = (JythonService) env.lookup(JythonService.class);
    } catch(NotFoundException e) {
      throw new IllegalStateException(
          "Could not find instance of " + JythonService.class);
    }
  }

  public void setModule(String module) throws Exception {
    _pyModule = module;
  }  

  public void setClass(String clazz) throws Exception {
    _pyClass = clazz;
  }
 
  public Object getPythonObjectInstance(){
    if(_pyObj == null){
      throw new IllegalStateException("Python object not initialized");
    }
    return _pyObj;
  }
  
  //////// service methods ////////
  
  //public void init() throws Exception{
  
  public Object onCreate() throws ConfigurationException{
    if(_pyClass == null){
      throw new ConfigurationException("'class' not set");
    }    
    if(_pyModule == null){
      throw new ConfigurationException("'module' not set");
    } 
    PythonInterpreter py = _jython.getInterpreter();
    py.exec("from " + _pyModule + " import " + _pyClass);
    py.exec("service=" + _pyClass + "()");
    try{
      _pyObj = py.get("service", Class.forName(Object.class.getName()));
    }catch(ClassNotFoundException e){
      throw new ConfigurationException("Could not acquire Jython service", e);
    }
    
    if(_pyObj instanceof EnvAware){
      ((EnvAware)_pyObj).setEnv(_env);
    }
    
    if(_configProps.size() > 0){
      Iterator props = _configProps.entrySet().iterator();
      while(props.hasNext()){
        Map.Entry prop = (Map.Entry)props.next();
        Method m = findMethod("set", (String)prop.getKey(), _pyObj);
        if(m == null){
          m = findMethod("add", (String)prop.getKey(), _pyObj);
        }
        if(m == null){
          
          Property beanProp = new Property((String)prop.getKey());
          py.set("param", prop.getValue());
          String attrName = "set" + capitalize((String)prop.getKey());
          PyProxy proxy = (PyProxy)_pyObj;
          try{
            beanProp.set(proxy._getPyInstance(), prop.getValue());
            //proxy._getPyInstance().invoke(attrName.intern(), Py.java2py(prop.getValue()));
          }catch(PyException pe){
            attrName = "add" + capitalize((String)prop.getKey());
            try{
              proxy._getPyInstance().invoke(attrName.intern(), Py.java2py(prop.getValue()));
            }catch(PyException pe2){
              throw new ConfigurationException("Could not find method for property '" 
                + prop.getKey() + "' on instance: " + _pyObj,  pe);              
            }
          }
        }
        else{
          try{
            m.invoke(_pyObj, new Object[]{prop.getValue()});
          }catch(InvocationTargetException e){
            throw new ConfigurationException("Could not assign value " + prop.getValue() + 
              " for property '" + prop.getKey() + "' on instance: " + _pyObj);              
          }catch(IllegalAccessException e){
            throw new ConfigurationException("Could not invoke method " + m + 
              " for property '" + prop.getKey() + "' on instance: " + _pyObj);                          
          }
        }
      }
    }    
    return _pyObj;
  }
  
  public void start() throws Exception{}
  
  public void dispose(){}
  
  public void handleObject(String name, Object o) throws ConfigurationException{
    _configProps.put(name, o);
  }
  
  
  private Method findMethod(String prefix, String name, Object param){
    Method[] m = _pyObj.getClass().getMethods();
    String methodName = prefix + capitalize(name);
    for(int i = 0; i < m.length; i++){
      if(m[i].getName().equals(methodName) && 
         m[i].getParameterTypes().length == 1 &&
         m[i].getParameterTypes()[0].isAssignableFrom(param.getClass())){
         return m[i];
      }
    }
    return null;
  }
  
  private String capitalize(String name){
    StringBuffer newName = new StringBuffer();
    newName.append(Character.toUpperCase(name.charAt(0)));
    if(name.length() > 1){
      newName.append(name.substring(1));
    }
    return newName.toString();
  }
  
}
  
