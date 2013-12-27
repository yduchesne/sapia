/*
 * ClassType.java
 *
 * Created on June 28, 2005, 7:55 PM
 */

package org.sapia.soto.config.types;

import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 *
 * @author yduchesne
 */
public class ClassType implements ObjectCreationCallback{
  
  private String _name;
  
  /** Creates a new instance of ClassType */
  public ClassType() {
  }
  
  public void setName(String name){
    _name = name;
  }
  
  public Object onCreate() throws ConfigurationException{
    if(_name == null)
      throw new ConfigurationException("No class name specified");
    try{
      return Class.forName(_name);
    }catch(ClassNotFoundException e){
      throw new ConfigurationException("Could not load class object", e);
    }
  }
  
}
