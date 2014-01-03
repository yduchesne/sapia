package org.sapia.regis.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sapia.regis.RWNode;
import org.sapia.regis.type.BuiltinType;

public class Form {
  
  private Map _fields = new HashMap();
  private String _type;
  
  public Form(String type){
    _type = type;
  }
  public String getType(){
    return _type;
  }
  
  public Field createField(BuiltinType type, String name){
    Field f = new Field(type, name);
    if(_fields.containsKey(name)){
      throw new IllegalArgumentException("Field already defined for: " + name);
    }
    f.setDisplayOrder(_fields.size());
    _fields.put(name, f);
    return f;
  }
  
  public void addField(Field f){
    if(f.getName() == null){
      throw new IllegalArgumentException("Field name not set");
    }
    if(_fields.containsKey(f.getName())){
      throw new IllegalArgumentException("Field already defined for: " + f.getName());
    }
    _fields.put(f.getName(), f);
  }
  
  public List getFields(){
    List fields = new ArrayList(_fields.values());
    Collections.sort(fields);
    return fields;
  }
  
  public Field getField(String name){
    Field f = (Field)_fields.get(name);
    if(f != null){
      return f;
    }    
    throw new IllegalArgumentException("No field for: " + name);
  }
  
  public boolean containsField(String name){
    return _fields.containsKey(name);
  }
  
  public FormInstance getInstance(RWNode node){
    return new FormInstance(this, node);
  }
  
  void assign(RWNode instance, Map props){
    for (Iterator i = _fields.values().iterator(); i.hasNext();) {
      Field f = (Field)i.next();
      f.set(instance, props);
    }    
  }
  

}
