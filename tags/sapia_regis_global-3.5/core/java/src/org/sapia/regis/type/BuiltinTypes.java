package org.sapia.regis.type;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.sapia.regis.Node;

/**
 * This class holds the various predefined built-in types.
 * 
 * @see org.sapia.regis.type.BuiltinType
 * 
 * @author yduchesne
 *
 */
public class BuiltinTypes {
  
  private static Map types = new HashMap();

  /**
   * Corresponds to the "string" type (<code>java.lang.String</code>).
   */
  public static final BuiltinType STRING_TYPE = 
    new BuiltinType(){
       public String getName() {return "string";}
       public Object parse(String value) {return value;}
       public String toString(Object value) {return (String)value;}
       public boolean isAssignable(Object value) {return value instanceof String;}
       public boolean isAssignable(Class clazz) {return clazz.equals(String.class);}
       public Object getProperty(String propName, Node node) {return node.getProperty(propName).asString();}
    };
    
  /**
   * Corresponds to the "file" type (<code>java.io.File</code>).
   */
  public static final BuiltinType FILE_TYPE = 
    new BuiltinType(){
       public String getName() {return "file";}
       public Object parse(String value) {return new File(value);}
       public String toString(Object value) {return ((File)value).getAbsolutePath();}
       public boolean isAssignable(Object value) {return value instanceof File;}
       public boolean isAssignable(Class clazz) {return clazz.equals(File.class);}
       public Object getProperty(String propName, Node node) {return new File(node.getProperty(propName).asString());}
    };      
  
    /**
     * Corresponds to the "boolean" type (<code>java.lang.Boolean</code>).
     */    
  public static final BuiltinType BOOLEAN_TYPE = 
    new BuiltinType(){
       public String getName() {return "boolean";}
       public Object parse(String value) {return new Boolean(value);}
       public String toString(Object value) {return ((Boolean)value).toString();}
       public boolean isAssignable(Object value) {return value instanceof Boolean;}
       public boolean isAssignable(Class clazz) {return clazz.equals(Boolean.class) || clazz.equals(boolean.class);}       
       public Object getProperty(String propName, Node node) {return new Boolean(node.getProperty(propName).asBoolean());}       
    };
    
    /**
     * Corresponds to the "int" type (<code>java.lang.Integer</code>).
     */    
  public static final BuiltinType INT_TYPE = 
    new BuiltinType(){
       public String getName() {return "int";}
       public Object parse(String value) {return new Integer(value);}
       public String toString(Object value) {return ((Integer)value).toString();}
       public boolean isAssignable(Object value) {return value instanceof Integer;}
       public boolean isAssignable(Class clazz) {return clazz.equals(Integer.class) || clazz.equals(int.class);}       
       public Object getProperty(String propName, Node node) {return new Integer(node.getProperty(propName).asInt());}       
    };    
    
    /**
     * Corresponds to the "long" type (<code>java.lang.Long</code>).
     */    
  public static final BuiltinType LONG_TYPE = 
    new BuiltinType(){
       public String getName() {return "long";}
       public Object parse(String value) {return new Long(value);}
       public String toString(Object value) {return ((Long)value).toString();}
       public boolean isAssignable(Object value) {return value instanceof Long;}
       public boolean isAssignable(Class clazz) {return clazz.equals(Long.class) || clazz.equals(long.class);}
       public Object getProperty(String propName, Node node) {return new Long(node.getProperty(propName).asLong());}       
    };      

    /**
     * Corresponds to the "float" type (<code>java.lang.Float</code>).
     */    
  public static final BuiltinType FLOAT_TYPE = 
    new BuiltinType(){
       public String getName() {return "float";}
       public Object parse(String value) {return new Float(value);}
       public String toString(Object value) {return ((Float)value).toString();}
       public boolean isAssignable(Object value) {return value instanceof Float;}
       public boolean isAssignable(Class clazz) {return clazz.equals(Float.class) || clazz.equals(float.class);}
       public Object getProperty(String propName, Node node) {return new Float(node.getProperty(propName).asFloat());}       
    };      
    
    /**
     * Corresponds to the "double" type (<code>java.lang.Double</code>).
     */    
  public static final BuiltinType DOUBLE_TYPE = 
    new BuiltinType(){
       public String getName() {return "double";}
       public Object parse(String value) {return new Double(value);}
       public String toString(Object value) {return ((Double)value).toString();}
       public boolean isAssignable(Object value) {return value instanceof Double;}
       public boolean isAssignable(Class clazz) {return clazz.equals(Double.class) || clazz.equals(double.class);}
       public Object getProperty(String propName, Node node) {return new Double(node.getProperty(propName).asDouble());}       
    };

  static{
    types.put(BOOLEAN_TYPE.getName(), BOOLEAN_TYPE);
    types.put(INT_TYPE.getName(), INT_TYPE);
    types.put(LONG_TYPE.getName(), LONG_TYPE);
    types.put(FLOAT_TYPE.getName(), FLOAT_TYPE);
    types.put(DOUBLE_TYPE.getName(), DOUBLE_TYPE);    
    types.put(STRING_TYPE.getName(), STRING_TYPE);
  }
  
  /**
   * @param name a type name
   * @return the <code>BuiltinType</code> corresponding to the given name.
   */
  public static BuiltinType getTypeFor(String name){
    BuiltinType type = (BuiltinType)types.get(name);
    if(type == null){
      throw new IllegalStateException("Invalid type name: " + name);
    }
    return type;
  }
  
  /**
   * @param value an <code>Object</code>
   * @return the <code>BuiltinType</code> of the given value, or 
   * <code>null</code> if the value does not correspond to a built-in
   * type.
   */
  public static BuiltinType getTypeFor(Object value){
    Iterator itr = types.values().iterator();
    while(itr.hasNext()){
      BuiltinType type = (BuiltinType)itr.next();
      if(type.isAssignable(value)){
        return type;
      }
    }
    return null;
  }
  
  /**
   * @param value a <code>Class</code>
   * @return the <code>BuiltinType</code> corresponding to the given class, or 
   * <code>null</code> if the class does not correspond to a built-in
   * type.
   */  
  public static BuiltinType getTypeFor(Class clazz){
    Iterator itr = types.values().iterator();
    while(itr.hasNext()){
      BuiltinType type = (BuiltinType)itr.next();
      if(type.isAssignable(clazz)){
        return type;
      }
    }
    return null;    
  }
}
