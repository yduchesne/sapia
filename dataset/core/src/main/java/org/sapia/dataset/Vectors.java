package org.sapia.dataset;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.sapia.dataset.impl.DefaultVector;

public class Vectors {

  private Vectors() {
  }
  
  /**
   * @param values the values with which to create a {@link Vector}.
   * @return the {@link Vector} that was created.
   */
  public static Vector vector(Object...values) {
    return new DefaultVector(values);
  }
  
  /**
   * @param values a {@link List} of values.
   * @return the {@link Vector} that was created, holding the given values.
   */
  public static Vector vector(List<Object> values) {
    return new DefaultVector(values);
  }
  
  /**
   * @param c the {@link Comparator} to use to sort the given vector's values.
   * @param toSort the {@link Vector} to sort.
   * @return a new {@link Vector}, with its values sorted.
   */
  public static Vector sort(Comparator<Object> c, Vector toSort) {
    Object[] values = toSort.toArray();
    Arrays.sort(values, c);
    return new DefaultVector(values);
  }
}
