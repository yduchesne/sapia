package org.sapia.regis.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompositeHashMap extends HashMap
  implements Serializable{
  
  static final long serialVersionUID = 1L;
  
  private List _children = new ArrayList();
 
  public CompositeHashMap(){}
  
  public CompositeHashMap addChild(Map child){
    _children.add(0, child);
    return this;
  }
  
  public CompositeHashMap addAncestor(Map ancestor){
    _children.add(ancestor);
    return this;
  }
  
  public Object get(Object name){
    Object value = super.get(name);
    if(value != null){
      return value;
    }
    for(int i = 0; i < _children.size(); i++){
      Map child = (Map)_children.get(i);
      value = child.get(name);
      if(value != null) return value;
    }
    return super.get(name);
  }
  
}
