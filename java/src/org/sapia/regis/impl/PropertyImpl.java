package org.sapia.regis.impl;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.sapia.regis.Property;
import org.sapia.regis.type.BuiltinType;
import org.sapia.regis.type.BuiltinTypes;

public class PropertyImpl implements Property, Serializable{
  
  static final long serialVersionUID = 1L;
  
  private static final String ON   = "on";
  private static final String TRUE = "true";
  private static final String YES  = "yes";  
  
  protected String _name, _value;
  
  public PropertyImpl(String name, String value){
    _name = name;
    _value = value;
  }
  
  public String getKey(){
    return _name;
  }

  public boolean asBoolean() {
    checkNull();
    return _value.equalsIgnoreCase(ON) ||
      _value.equalsIgnoreCase(TRUE) ||
      _value.equalsIgnoreCase(YES);
  }

  public double asDouble() {
    checkNull();
    return Double.parseDouble(_value);
  }

  public float asFloat() {
    checkNull();
    return Float.parseFloat(_value);
  }

  public int asInt() {
    checkNull();
    return Integer.parseInt(_value);
  }

  public long asLong() {
    checkNull();
    return Long.parseLong(_value);
  }

  public String asString() {
    checkNull();
    return _value;
  }
  
  public List asList() {
   return asList(BuiltinTypes.STRING_TYPE);
  }
  
  public List asList(BuiltinType type) {
    checkNull();
    List toReturn = new LinkedList();
    StringTokenizer tokenizer = new StringTokenizer(_value);
    while(tokenizer.hasMoreTokens()){
      toReturn.add(type.parse(tokenizer.nextToken()));
    }
    return toReturn;
  }

  public String getValue() {
    return _value;
  }

  public boolean isNull() {
    return _value == null;
  }
  
  protected void checkNull(){
    if(_value == null){
      throw new IllegalStateException("Could not convert; value is null for property: " + _name);
    }
  }
  
  public String toString(){
    return new StringBuffer("[")
      .append("key=").append(_name).append(",")
      .append("value=").append(_value).append("]")
      .toString();
  }

}
