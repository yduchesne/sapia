package org.sapia.regis.type;

import org.sapia.regis.Node;

/**
 * This interface specifies the behavior of "builtin" data types.
 * 
 * @see org.sapia.regis.type.BuiltinTypes
 * 
 * @author yduchesne
 *
 */
public interface BuiltinType {
  
  /**
   * @return the name of this type.
   */
  public String getName();
  
  /**
   * @param value a <code>String</code> value
   * @return the <code>Object</code> value that could be parsed from the given string.
   */
  public Object parse(String value);
  
  /**
   * @param value an <code>Object</code> value
   * @return the <code>String</code> representation for the given string.
   */  
  public String toString(Object value);
  
  /**
   * @param value an <code>Object</code>
   * @return <code>true</code> if the given object's type corresponds to this type.
   */
  public boolean isAssignable(Object value);

  /**
   * @param value an <code>Object</code>
   * @return <code>true</code> if the given class corresponds to this type.
   */  
  public boolean isAssignable(Class clazz);
  
  /**
   * @param propName the name of the property whose value should be returned.
   * @param node a <code>Node</code>.
   * @return the <code>Object</code> corresponding to the value of the required property.
   */
  public Object getProperty(String propName, Node node);

}
