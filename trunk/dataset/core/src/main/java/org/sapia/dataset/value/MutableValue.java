package org.sapia.dataset.value;

/**
 * Implement a mutabe {@link Value} (this class is not thread-safe).
 * 
 * 
 * @author yduchesne
 *
 */
public class MutableValue implements Value {
  
  private double value;
  
  @Override
  public double get() {
    return value;
  }
  
  /**
   * @param by a <code>double</code> value by which to increase this instance's own value.
   */
  public MutableValue increase(double by) {
    value += by;
    return this;
  }
  
  /**
   * @param value a <code>double</code> value to assign to this instance.
   */
  public void set(double value) {
    this.value = value;
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
