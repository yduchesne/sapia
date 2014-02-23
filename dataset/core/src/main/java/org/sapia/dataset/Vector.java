package org.sapia.dataset;

/**
 * A {@link Vector} is a simple, immutable array-like data structure. Vectors are typically used
 * as rows in {@link Dataset}s or {@link RowSet}s.
 * 
 * @author yduchesne
 *
 */
public interface Vector extends Iterable<Object> {

  /**
   * @return this instance's number of items.
   */
  public int size();
  
  /**
   * @param index the index of the item to return.
   * @return the {@link Object} at the given index.
   * @throws IllegalArgumentException if the index is invalid.
   */
  public Object get(int index) throws IllegalArgumentException;
  
  /**
   * @param indices the indices whose corresponding values should be returned.
   * @return a new {@link Vector} holding the values at the given indices.
   * @throws IllegalArgumentException if anyone of the given indices is invalid.
   */
  public Vector subset(int...indices) throws IllegalArgumentException;
 
  /**
   * @return an array holding this instance's values.
   */
  public Object[] toArray();
}
