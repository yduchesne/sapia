package org.sapia.dataset.value;

/**
 * Provides {@link Value}-related utility methods.
 * 
 * @author yduchesne
 *
 */
public class Values {
  
  private Values() {
  }

  /**
   * @param o evaluates the <code>double</code> value corresponding to the 
   * given object.
   * @return the <code>double</code> value that was evaluated.
   */
  public static double doubleValue(Object o) {
    if (o == null) {
      return 0;
    } else if (o instanceof Number) {
      return ((Number) o).doubleValue();
    } else if (o instanceof Value) {
      return ((Value) o).get();
    } else {
      return 0;
    }
  }
}
