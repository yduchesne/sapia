/*
 * Converter.java
 *
 * Created on December 3, 2005, 11:53 PM
 */

package org.sapia.soto.jython.bean;

/**
 * This interface specifies "conversion" behavior (transforming data represented
 * as a string to the appropriate object representation).
 *
 * @author yduchesne
 */
public interface Converter {
  
  public Object convert(String data);
  
}
