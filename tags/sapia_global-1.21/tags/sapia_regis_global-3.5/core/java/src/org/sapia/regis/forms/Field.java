package org.sapia.regis.forms;

import java.util.Map;

import org.sapia.regis.Node;
import org.sapia.regis.Property;
import org.sapia.regis.RWNode;
import org.sapia.regis.type.BuiltinType;

public class Field implements Comparable{
  
  private BuiltinType type;
  private String name;
  private boolean mandatory;
  private int displayOrder;
  
  Field(BuiltinType type, String name){
    this.type = type;
    this.name = name;
  }
  
  public String getName(){
    return name;
  }
  
  public BuiltinType getType(){
    return type;
  }
  
  public void setMandatory(boolean mandatory){
    this.mandatory = mandatory;
  }
  
  public boolean isMandatory(){
    return mandatory;
  }
  
  public void set(RWNode instance, String value){
    type.parse(value);
    instance.setProperty(name, value);
  }  
  
  public void set(RWNode instance, Object value){
    if(!type.isAssignable(value)){
      throw new IllegalArgumentException("Cannot assign value for field: " + name +
          "; expected value of type: " + type.getName());
    }
    instance.setProperty(name, type.toString(value));
  }
  
  public void set(RWNode instance, Map properties){
    Object value = properties.get(name);
    if(value == null && isMandatory()){
      throw new IllegalStateException("Property: " + name + " is mandatory");
    }
    if(value instanceof String){
      value = type.parse((String)value);
    }
    else if(!type.isAssignable(value)){
      throw new IllegalArgumentException("Cannot assign value for field: " + name +
          "; expected value of type: " + type.getName());
    }
    instance.setProperty(name, type.toString(value));
  }  
  
  public Object get(Node instance){
    Property prop = instance.getProperty(name);
    if(prop.isNull()) return null;
    return type.parse(prop.getValue());
  }
  
  public int getDisplayOrder() {
    return displayOrder;
  }

  public void setDisplayOrder(int displayOrder) {
    this.displayOrder = displayOrder;
  }  
  
  public int hashCode(){
    return name.hashCode();
  }
  
  public boolean equals(Object o){
    if(o instanceof Field){
      return ((Field)o).getName().equals(name);
    }
    return o == this;
  }
  
  public int compareTo(Object f) {
    return displayOrder - ((Field)f).getDisplayOrder();
  }

}
