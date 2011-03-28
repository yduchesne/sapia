package org.sapia.soto.config.types;

import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class ConstantType  implements ObjectCreationCallback {

  private String _name, _className;

  public void setName(String name) {
    _name = name;
  }
  
  public void setClass(String className){
    _className = className;
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    if(_name == null){
      throw new ConfigurationException("Constant value not set");
    }
    if(_className == null){
      int idx = _name.lastIndexOf(".");
      if(idx <= 0){
        throw new ConfigurationException("Constant class name not set");
      }
      _className = _name.substring(0, idx);
      _name = _name.substring(idx+1);
    }
    try{
      Class clazz = Class.forName(_className);
      return clazz.getField(_name).get(null);
    }catch(Exception e){
      throw new ConfigurationException("Could not found constant " + _name  + 
          " on class " + _className, e);
    }
  }
}
