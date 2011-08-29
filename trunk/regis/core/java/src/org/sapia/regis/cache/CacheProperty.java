package org.sapia.regis.cache;

import org.sapia.regis.Property;
import org.sapia.regis.impl.PropertyImpl;
import org.sapia.regis.type.BuiltinTypes;
import org.sapia.regis.type.Value;

public class CacheProperty extends PropertyImpl{
  
  static final long serialVersionUID = 1L;
  
  private static final int BOOLEAN = 0;
  private static final int INT = 1;
  private static final int LONG = 2;  
  private static final int FLOAT = 3;  
  private static final int DOUBLE = 4;

  private transient CacheNode node;
  private long lastModified;
  
  private Value[] values = new Value[]{
    Value.newInstance(BuiltinTypes.BOOLEAN_TYPE),
    Value.newInstance(BuiltinTypes.INT_TYPE),
    Value.newInstance(BuiltinTypes.LONG_TYPE),
    Value.newInstance(BuiltinTypes.FLOAT_TYPE),
    Value.newInstance(BuiltinTypes.DOUBLE_TYPE)    
  }; 
  
  CacheProperty(String name, String value, CacheNode node){
    super(name, value);
    this.node = node;
    this.lastModified = node.lastModifChecksum();
  }
  
  public String getValue() {
    refresh();
    super.checkNull();
    return super.getValue();
  }
  
  public boolean asBoolean() {
    refresh();
    super.checkNull();
    return ((Boolean)doGetValue(BOOLEAN)).booleanValue();
  }
  
  public int asInt() {
    refresh();
    super.checkNull();
    return ((Integer)doGetValue(INT)).intValue();
  }
  
  public long asLong() {
    refresh();
    super.checkNull();
    return ((Long)doGetValue(LONG)).longValue();
  }
  
  public float asFloat() {
    refresh();
    super.checkNull();
    return ((Float)doGetValue(FLOAT)).floatValue();
  }
  
  public double asDouble() {
    refresh();
    super.checkNull();
    return ((Double)doGetValue(DOUBLE)).floatValue();
  }
  
  public String asString() {
    refresh();
    return super.asString();
  }
  
  private Object doGetValue(int index){
    Object value = values[index].get();
    if(value == null){
      String thisValue = _value;
      Value v = values[index];
      values[index].set(v.getType().parse(thisValue));
      value = values[index].get();
    }
    return value;
  }
  
  private void refresh(){
    if(node == null){
      return;
    }
    long checkSum = node.lastModifChecksum();
    if(lastModified != checkSum){
      synchronized(this){
        if(lastModified != checkSum){
          Property prop = node.getProperty(getKey());
          super._value = prop.getValue();
          for(int i = 0; i < values.length; i++){
            values[i].nullify();
          }
          lastModified = checkSum;
        }
      }
    }
  }

}
