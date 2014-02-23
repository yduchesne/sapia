package org.sapia.dataset.value;

/**
 * Implements the {@link Value} interface over the <code>double</code> type.
 * 
 * @author yduchesne
 *
 */
public class DoubleValue implements Value {
  
  private double value;
  
  /**
   * @param number a {@link Number}.
   */
  public DoubleValue(Number number) {
    this(number == null ? 0 : number.doubleValue());
  }
  
  /**
   * @param value a <code>double</code> value.
   */
  public DoubleValue(double value) {
    this.value = value;
  }
  
  @Override
  public double get() {
    return value;
  }
  
  @Override
  public int hashCode() {
    return (int) value;
  }
  
  @Override
  public boolean equals(Object o) {
    if (o instanceof Value) {
      return ((Value) o).get() == value;
    }
    return false;
  }
  
  @Override
  public String toString() {
    return Double.toString(value);
  }
}
