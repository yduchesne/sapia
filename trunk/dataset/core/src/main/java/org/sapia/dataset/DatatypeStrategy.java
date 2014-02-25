package org.sapia.dataset;

/**
 * An instance of this interface is associated to a {@link Datatype}.
 * 
 * @author yduchesne
 *
 */
public interface DatatypeStrategy {

  /**
   * @param value a value.
   * @return <code>true</code> if the given value is assignable to the 
   * {@link Datatype} to which this instance corresponds.
   */
  public boolean isAssignableFrom(Object value);
  
  /**
   * @param currentValue the current value.
   * 
   * @return
   */
  public Object add(Object currentValue, Object toAdd);
  
  /**
   * @param value the value whose type corresponds to this instance's {@link Datatype}.
   * @param operand the operand with which to compare the given value.
   * @return 0 if the both parameters are deemed equal, a number less than 0 if <code>value</code> is deemed
   * lower/smaller than <code>operand</code>, or larger than 0 if <code>value</code> is deemed 
   * greater/larger than <code>operand</code>.
   */
  public int compareTo(Object value, Object operand);
}
