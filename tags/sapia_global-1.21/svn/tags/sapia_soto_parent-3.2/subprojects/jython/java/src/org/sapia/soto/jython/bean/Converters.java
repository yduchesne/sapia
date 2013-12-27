/*
 * Converters.java
 *
 * Created on December 3, 2005, 11:55 PM
 */

package org.sapia.soto.jython.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * This class encapsulates <code>Converter</code> implementations that can
 * be retrieved by passing in the "type" of converter that is desired (the
 * different possible type identifiers are kept as constants of this class).
 * <p>
 * This class also presents a conversion method as a shortcut.
 *
 * @author yduchesne
 */
public class Converters {
  
  public static Converter BOOLEAN = new Converter(){
                                      public Object convert(String data){
                                        return new Boolean(
                                          data.equalsIgnoreCase("on") ||
                                          data.equalsIgnoreCase("true") ||
                                          data.equalsIgnoreCase("yes")
                                        );
                                      }
                                    };
                                    
  public static Converter SHORT = new Converter(){
                                      public Object convert(String data){
                                        return new Short(data);
                                      }
                                    };                                    
                                    
  public static Converter INT   = new Converter(){
                                      public Object convert(String data){
                                        return new Integer(data);
                                      }
                                    };                                                                        
                                    
  public static Converter LONG = new Converter(){
                                      public Object convert(String data){
                                        return new Long(data);
                                      }
                                    };                 

  public static Converter FLOAT = new Converter(){
                                      public Object convert(String data){
                                        return new Float(data);
                                      }
                                    };                  
                                    
  public static Converter DOUBLE = new Converter(){
                                      public Object convert(String data){
                                        return new Double(data);
                                      }
                                    };                   
                                    
                                    
  public static final String BOOLEAN_TYPE = "boolean";
  
  public static final String SHORT_TYPE = "short";  
  
  public static final String INT_TYPE = "int";  
  
  public static final String LONG_TYPE = "long";  
  
  public static final String FLOAT_TYPE = "float";    
  
  public static final String DOUBLE_TYPE = "double";  
  
  private static Map converters = new HashMap();
  
  static{
    converters.put(BOOLEAN_TYPE, BOOLEAN);
    converters.put(SHORT_TYPE, SHORT);
    converters.put(INT_TYPE, INT);
    converters.put(LONG_TYPE, LONG);
    converters.put(FLOAT_TYPE, FLOAT);
  }
  
  /** Creates a new instance of Converters */
  public Converters() {
  }
  
  public static Converter getConverterFor(String type){
    Converter conv = (Converter)converters.get(type);
    if(conv == null){
      throw new IllegalArgumentException("Unknown type: " + type);
    }
    return conv;
  }
  
  public static Object convert(String type, Object data){
    if(data instanceof String){
      return getConverterFor(type).convert((String)data);
    }
    else{
      return data;
    }
  }
  
  
}
