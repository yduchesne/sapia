package org.sapia.dataset;


/**
 * Holds a set of ordered {@link Vector}s corresponding to rows.
 * 
 * @author yduchesne
 *
 */
public interface RowSet extends Iterable<Vector> {
  
  /**
   * @return the number of rows in this {@link RowSet}.
   */
  public int size();

  /**
   * @param index an index.
   * @return the {@link Vector} corresponding to the row at the given index.
   * @throws IllegalArgumentException if the index is invalid.
   */
  public Vector get(int index) throws IllegalArgumentException;
}
