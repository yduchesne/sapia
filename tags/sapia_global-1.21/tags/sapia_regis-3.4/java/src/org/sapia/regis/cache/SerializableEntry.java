package org.sapia.regis.cache;

import java.io.Serializable;
import java.util.Map;

public class SerializableEntry implements Map.Entry, Serializable{
  
  static final long serialVersionUID = 1L;

  private Object key, value;
  
  public SerializableEntry(Object key, Object value){
    this.key = key;
    this.value = value;
  }
  
  public Object getKey() {
    return value;
  }
  
  public boolean equals(Object obj) {
    if(obj instanceof SerializableEntry){
      return key.equals(((SerializableEntry)obj).key);
    }
    return false;
  }
  
  public Object getValue() {
    return value;
  }
  
  public int hashCode() {
    return key.hashCode();
  }
  
  public Object setValue(Object val) {
    return value = val;
  }
}
