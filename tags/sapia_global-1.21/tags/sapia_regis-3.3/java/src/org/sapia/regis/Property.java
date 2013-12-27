package org.sapia.regis;

import java.util.List;

import org.sapia.regis.type.BuiltinType;

/**
 * Models a configuration property.
 * 
 * @author yduchesne
 */
public interface Property{
  
  /**
   * @return this instance's value, or <code>null</code>
   * if no such value exists.
   */
  public String getValue();
  
  /**
   * @return this instance's key.
   */
  public String getKey();
  
  /**
   * @return <code>true</code> if this instance has no value.
   */
  public boolean isNull();
  
  /**
   * @return this instance's string value.
   * @throws IllegalStateException if this instance has no value.
   * @see #isNull()
   */
  public String asString();
  
  /**
   * @return this instance's integer value.
   * @throws IllegalStateException if this instance has no value.
   * @throws NumberFormatException if this instance could not be
   * converted to an integer.
   * @see #isNull()
   */  
  public int asInt();
  
  /**
   * @return this instance's long value.
   * @throws IllegalStateException if this instance has no value.
   * @throws NumberFormatException if this instance could not be
   * converted to a long. 
   * @see #isNull()
   */    
  public long asLong();
  
  /**
   * @return this instance's float value.
   * @throws IllegalStateException if this instance has no value.
   * @throws NumberFormatException if this instance could not be
   * converted to a float.
   * @see #isNull()
   */    
  public float asFloat();
  
  /**
   * @return this instance's double value.
   * @throws IllegalStateException if this instance has no value.
   * @throws NumberFormatException if this instance could not be
   * converted to a double.
   * @see #isNull()
   */    
  public double asDouble();
  
  /**
   * @return <code>true</code> if this instance's value corresponds
   * to <code>on</code>, <code>yes</code>, or <code>true</code> - 
   * internal comparison is case-insensitive.
   * 
   * @throws IllegalStateException if this instance has no value.
   * @see #isNull()
   */    
  public boolean asBoolean();
  
  /**
   * @return the <code>List</code> of <code>String</code> values held in this
   *  property.
   *  
   * @see #asList(BuiltinType)
   */
  public List asList();

  /**
   * @return the <code>List</code> of values held in this property (in fact, the value
   * is expected to be a comma-delimited list of values).
   */
  
  public List asList(BuiltinType type);  

}
