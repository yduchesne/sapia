package org.sapia.dataset;

/**
 * Abstracts a given row in either a {@link RowSet} or a {@link Dataset}.
 * Meant to provide an abstraction on top of the {@link Vector} interface, allowing
 * the lookup of values by column name.
 * 
 * @author yduchesne
 *
 */
public interface RowResult {

  /**
   * @param index a column index.
   * @return the value at the given index.
   * @throws IllegalArgumentException if the index is invalid.
   */
  public Object get(int index) throws IllegalArgumentException;

  /**
   * @param name a column name.
   * @return the value corresponding to the given name.
   * @throws IllegalArgumentException if the column name is invalid.
   */
  public Object get(String name) throws IllegalArgumentException;
  
  
}
