package org.sapia.dataset.transform.range;

import java.util.Comparator;

import org.sapia.dataset.Datatype;
import org.sapia.dataset.format.Format;
import org.sapia.dataset.format.SelfFormattable;
import org.sapia.dataset.util.Checks;
import org.sapia.dataset.value.Value;
import org.sapia.dataset.value.Values;

/**
 * Models a range, which is composed of a minimum bound and a maximum bound. 
 * The minimum is inclusive, as the maximum is exclusive. Therefore, a value v will
 * be considered "within" the range if it's greater than or equal to the minimum, or
 * lower than the maximum.
 *  
 * @author yduchesne
 *
 */
public class Range<T> implements Value, SelfFormattable {
  
  private Comparator<T> comp;
  private T min, max;
  
  /**
   * @param comp the {@link Comparator} to use to compare the min and max values.
   * @param min a minimum value.
   * @param max a maximum value.
   */
  public Range(Comparator<T> comp, T min, T max) {
    this.comp = comp;
    int c = compare(min, max);
    Checks.isTrue(c <= 0, "Min must be lower than or equal to max (got: min = %s, max = %s)", min, max);
    this.min  = min;
    this.max  = max;
  }

  @Override
  public double get() {
    return (Values.doubleValue(min) + Values.doubleValue(max)) / 2;
  }
  
  @Override
  public String format(Datatype type, Format format) {
    return "[" + format.formatValue(type, min) 
        + " - " + format.formatValue(type, max) + "]";
  }
  
  @Override
  public String toString() {
    return "[" + min  + " - " + max + "]";
  }
  
  private int compare(T left, T right) {
    if (left == null) {
      return 1;
    } else if (right == null) {
      return -1;
    } 
    return comp.compare(left, right);
  }
}
