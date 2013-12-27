/*
 * NewType.java
 *
 * Created on June 28, 2005, 8:27 PM
 */

package org.sapia.soto.config.types;

import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 *
 * @author yduchesne
 */
public class NewType implements ObjectCreationCallback{
  
  private String _class;
  
  /** Creates a new instance of NewType */
  public NewType() {
  }
  
  public void setClass(String clazz){
    _class = clazz;
  }
  
  public Object onCreate() throws ConfigurationException{
    if(_class == null){
      throw new ConfigurationException("Class not set");
    }
    try{
      return Class.forName(_class).newInstance();
    }catch(ClassNotFoundException e){
      throw new ConfigurationException("Could not find class: " + _class, e);
    }catch(InstantiationException e){
      throw new ConfigurationException("Problem while instantiating object of class: " + 
        _class, e.getCause() == null ? e : e.getCause());
    }catch(IllegalAccessException e){
      throw new ConfigurationException("Could not instantiate object for class class: " + 
        _class + "; make sure class has a public no-arg constructor", e);      
    }
  }
  
}
