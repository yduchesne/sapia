/*
 * Variable.java
 *
 * Created on November 8, 2005, 3:10 PM
 */

package org.sapia.soto.configuration.jconfig;

import org.sapia.soto.config.NullObjectImpl;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 *
 * @author yduchesne
 */
public class Variable implements ObjectCreationCallback{
  
  private String _name, _value;
  private ConfigurationDef _owner;
  
  /** Creates a new instance of Variable */
  public Variable(ConfigurationDef def) {
    _owner = def;
  }
  
  public void setName(String name){
    _name = name;
  }
  
  public void setValue(String value){
    _value = value;
  }
  
  
  public Object onCreate() throws ConfigurationException{
    if(_name != null && _value != null){
      _owner.addVariable(_name, _value);
    }
    return new NullObjectImpl();
  }
}
