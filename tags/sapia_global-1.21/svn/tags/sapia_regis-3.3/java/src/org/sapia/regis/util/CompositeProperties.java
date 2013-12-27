package org.sapia.regis.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CompositeProperties extends Properties
  implements Serializable{
  
  static final long serialVersionUID = 1L;
  
  private List _children = new ArrayList();
 
  public CompositeProperties(){}
  
  public CompositeProperties(Properties ancestor){
    super(ancestor);
  }  
  
  public Properties addChild(Properties child){
    _children.add(0, child);
    return this;
  }
  
  public Properties addAncestor(Properties ancestor){
    _children.add(ancestor);
    return this;
  }
  
  public String getProperty(String name){
    for(int i = 0; i < _children.size(); i++){
      Properties child = (Properties)_children.get(i);
      String value = child.getProperty(name);
      if(value != null) return value;
    }
    return super.getProperty(name);
  }
  
  public String getProperty(String name, String defaultVal){
    for(int i = 0; i < _children.size(); i++){
      Properties child = (Properties)_children.get(i);
      String value = child.getProperty(name);
      if(value != null) return value;
    }
    return super.getProperty(name, defaultVal);
  }  

}
