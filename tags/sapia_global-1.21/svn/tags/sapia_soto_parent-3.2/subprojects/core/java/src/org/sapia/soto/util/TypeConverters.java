package org.sapia.soto.util;

import java.util.HashMap;
import java.util.Map;

public class TypeConverters {

  static final Map TYPES;
  
  static{
    Map types = new HashMap();
    
    /// String
    
    types.put(String.class, new TypeConverter(){
      public Object convert(String value) throws IllegalArgumentException, RuntimeException {
        return value;
      }
    });
    
    /// BOOLEAN
    
    types.put(boolean.class, new TypeConverter(){
      public Object convert(String value) throws IllegalArgumentException, RuntimeException {
        return new Boolean(value != null && (
            value.equalsIgnoreCase("true") ||
            value.equalsIgnoreCase("on") || 
            value.equalsIgnoreCase("yes")
            ));
      }
    });    
    
    types.put(Boolean.class, types.get(boolean.class));
    
    /// SHORT
    
    types.put(short.class, new TypeConverter(){
      public Object convert(String value) throws IllegalArgumentException, RuntimeException {
        return new Short(value);
      }
    });
    
    types.put(Short.class, types.get(short.class));
    
    /// INTEGER    
    
    types.put(int.class, new TypeConverter(){
      public Object convert(String value) throws IllegalArgumentException, RuntimeException {
        return new Integer(value);
      }
    });    
    
    types.put(Integer.class, types.get(int.class));
    
    /// LONG
    
    types.put(long.class, new TypeConverter(){
      public Object convert(String value) throws IllegalArgumentException, RuntimeException {
        return new Long(value);
      }
    });    
    
    types.put(Long.class, types.get(long.class));    
    
    /// FLOAT
    
    types.put(float.class, new TypeConverter(){
      public Object convert(String value) throws IllegalArgumentException, RuntimeException {
        return new Float(value);
      }
    });    
    
    types.put(Float.class, types.get(float.class));    
    
    /// DOUBLE
    
    types.put(double.class, new TypeConverter(){
      public Object convert(String value) throws IllegalArgumentException, RuntimeException {
        return new Double(value);
      }
    });    
    
    types.put(Double.class, types.get(double.class));    
    
    TYPES = types;
  }
  
  public static Object convert(String value, Class targetType){
    TypeConverter conv = (TypeConverter)TYPES.get(targetType);
    if(conv == null){
      throw new IllegalArgumentException("Could not convert value: " + value +
          "; unhandled target type: " + targetType);
    }
    return conv.convert(value);
  }
}
