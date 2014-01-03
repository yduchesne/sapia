package org.sapia.regis.spring;

import java.text.ParseException;

/**
 * An instance of this interface is meant to allow extending the
 * {@link RegisAnnotationProcessor}'s conversion mechanism.
 * <p>
 * Implementations of this interface are added to a {@link RegisAnnotationProcessor},
 * forming a chain of responsibility. The processor interrogates them in sequence
 * at runtime, invoking the {@link #accepts(Class)} method; the first converter whose
 * <code>accept</code> method returns <code>true</code> is used by the processor.  
 * 
 * @author yduchesne
 */
public interface Converter {

  /**
   * This method returns <code>true</code> if this instance can convert
   * string values to instances of the given class.
   * 
   * @param type a {@link Class} object.
   * @return <code>true</code> if this instance can convert string
   * values to the given type, false otherwise.
   */
  public boolean accepts(Class<?> type);
  
  /**
   * Converts the given value into an instance of the passed in
   * class.
   * 
   * @param the {@link Class} for which an instance should be created
   * from the given value.
   * @param value the {@link String} value to convert.
   * @return the {@link Object} into which the given string was
   * converted.
   * 
   * @throws ParseException if the given value could not be parsed.
   */
  public Object convertFrom(Class<?> type, String value) throws ParseException;
}
