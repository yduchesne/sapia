package org.sapia.util.lang;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class can be used to implement application-specific constants.
 *  
 * @author yduchesne
 *
 */
public class Constant implements Serializable {
  
  static final long serialVersionUID = 1L;

  private int _value;
  private String _name;
  
  public Constant(String name, int value){
    _name = name;
    _value = value;
  }

  /**
   * @return this constant's name.
   */
  public String getName(){
    return _name;
  }
  
  /**
   * @return this constant's value.
   */  
  public int getValue(){
    return _value;
  }
  
  public int hashCode(){
    return _value;
  }
  
  public boolean equals(Object other){
    if(!(other instanceof Constant)){
      return false;
    }
    Constant cons = (Constant)other;
    return _value == cons.getValue() && _name.equals(cons.getName());
  }
  
  public String toString(){
    return new StringBuffer("[name=").append(_name)
      .append(", value=").append(_value).append("]").toString();
  }
  
  /**
   * @param constantClass a class that holds <code>constant</code> instances, 
   * has public fields.
   * @return the <code>Map</code> of constants corresponding to the public,
   * static, final fields of the <code>Constant</code> class.
   */
  public static Map getConstantsFrom(Class constantClass){
    Field[] fields = constantClass.getFields();
    Map constants = new HashMap();
    for(int i = 0; i < fields.length; i++){
      try{
        if((fields[i].getModifiers() & Modifier.STATIC) != 0 &&
           (fields[i].getModifiers() & Modifier.PUBLIC) != 0 &&
           (fields[i].getModifiers() & Modifier.FINAL) != 0 &&
           (fields[i].getType().isAssignableFrom(Constant.class))){
          Constant constant = (Constant)fields[i].get(null);
          if(constant.getName() == null){
            constant._name = fields[i].getName();
          }
          constants.put(constant.getName(), constant);
        }
      }catch(Exception e){
        throw new RuntimeException("Could not get constant for field: " + fields[i].getName());
      }
    }
    return constants;
  }
  
  /**
   * @param constants a <code>Map</code> of constant name to <code>Constant</code> instances.
   * @param value the value for which the corresponding <code>Constant</code> should
   * be retrieved.
   * @return the retrieved <code>Constant</code>, or <code>null</code> if no such constant
   * could be found.
   * 
   * @see #getConstantsFrom(Class)
   */
  public static Constant getConstant(Map constants, int value){
    Iterator itr = constants.values().iterator();
    while(itr.hasNext()){
      Constant cons = (Constant)itr.next();
      if(cons.getValue() == value){
        return cons;
      }
    }
    return null;
  }
  
}
