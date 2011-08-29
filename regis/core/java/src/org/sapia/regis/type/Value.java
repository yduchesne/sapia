package org.sapia.regis.type;

/**
 * Wraps an object value corresponding to a <code>BuiltinType</code> (the Value/BuiltinType
 * relation is analoguous to the Object/Primitive type relation).
 * 
 * @author yduchesne
 *
 */
public class Value {
  
  private BuiltinType type;
  private Object value;
  
  private Value(BuiltinType type, Object value){
    this.value = value;
    this.type = type;
  }
  
  /**
   * @return the <code>BuiltinType</code> of this instance.
   */
  public BuiltinType getType(){
    return type;
  }

  /**
   * Sets this instance's value, if the given object corresponds to this
   * instance's <code>BuiltinType</code>.
   * 
   * @param o an <code>Object</code>
   */
  public void set(Object o){
    if(o == null){
      this.value = o;
    }
    else if(type.isAssignable(o)){
      this.value = o;
    }
    else{
      throw new IllegalArgumentException("Invalid value: " + o + " for type: " + type.getName());
    }
  }
  
  /**
   * Sets this instance's value, if the given string can be parsed to a
   * value that corresponds to this instance's <code>BuiltinType</code>.
   * 
   * @param o a <code>String</code>.
   * @return the <code>Object</code> value that was parsed from the given string
   * and internally set to this instance.
   */
  public Object set(String o){
    if(o == null){
      this.value = o;
    }
    else if(type.isAssignable(o)){
      this.value = o;
    }
    else{
      this.value = this.type.parse(o);
    }
    return this.value;
  }

  /**
   * Sets the internal value of this instance to <code>null</code>.
   */
  public void nullify(){
    this.value = null;
  }
  
  /**
   * @return the internal value of this instance.
   */
  public Object get(){
    return value;
  }
  
  /**
   * @param type the <code>Builtin</code> type to which the returned value 
   * will correspond.
   * @return a <code>Value</code>.
   */
  public static Value newInstance(BuiltinType type){
    return new Value(type, null);
  }
  
  /**
   * @param type the <code>Builtin</code> type to which the returned value 
   * will correspond.
   * @param value a string value that corresponds to the passed in built-in type.
   * @return a <code>Value</code>.
   */  
  public static Value newInstance(BuiltinType type, String value){
    if(value == null){
      return new Value(type, null);
    }
    else{
      return new Value(type, type.parse(value));
    }
  }
  
  /**
   * @param type the <code>Builtin</code> type to which the returned value 
   * will correspond.
   * @param value an <code>Object</code> value that corresponds to the passed in built-in type.
   * @return a <code>Value</code>.
   */  
  public static Value newInstance(BuiltinType type, Object value){
    if(value == null){
      return new Value(type, null);
    }
    else if(type.isAssignable(value)){
      return new Value(type, value);
    }
    else{
      throw new IllegalArgumentException("Invalid value: " + value + " for type: " + type.getName());      
    }
  }    
  
}
