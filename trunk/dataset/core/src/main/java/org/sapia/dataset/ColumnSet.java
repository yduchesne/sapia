package org.sapia.dataset;

import java.util.List;

/**
 * Holds a set of columns - typically in a {@link Dataset}.
 * 
 * @author yduchesne
 *
 */
public interface ColumnSet extends Iterable<Column> {
  
  /**
   * @return this instance's unmodifiable {@link List} of {@link Column}
   * instances.
   */
  public List<Column> getColumns();
  
  /**
   * @return the indices of associated to this instance's {@link Column}s.
   */
  public int[] getColumnIndices();

  /**
   * @return the number of {@link Column}s that this instance holds.
   */
  public int size();

  /**
   * @param name a column name.
   * @return the {@link Column} at the given name.
   * @throws IllegalArgumentException if the given name is invalid.
   */
  public Column get(String name) throws IllegalArgumentException;
  
  /**
   * @param colIndex a column index.
   * @return the {@link Column} at the given index.
   * @throws IllegalArgumentException if the given index is invalid.
   */
  public Column get(int colIndex) throws IllegalArgumentException;
  
  /**
   * Returns a subset of this instance, that is: a new {@link ColumnSet} holding
   * this instance's {@link Column}s that have the given names.
   * <p>
   * The returned {@link Column}s keep their original index (see {@link Column#getIndex()}).
   * 
   * @param names one or more column names.
   * @return a new {@link ColumnSet}, holding the {@link Column}s corresponding 
   * to the given names.
   * @throws IllegalArgumentException if an invalid column name is provided.
   */
  public ColumnSet includes(String...names) throws IllegalArgumentException;

  /**
   * @param names a {@link List} of column names.
   * @return the {@link ColumnSet} corresponding to the specified subset.
   * @throws IllegalArgumentException if an invalid column name is provided.
   */
  public ColumnSet includes(List<String> names) throws IllegalArgumentException;

  /**
   * Returns a subset of this instance, that is: a new {@link ColumnSet} holding
   * this instance's {@link Column}s that DO NOT have the given names.
   * <p>
   * The returned {@link Column}s keep their original index (see {@link Column#getIndex()}).
   * 
   * @param names one or more column names.
   * @return a new {@link ColumnSet}, holding the {@link Column}s NOT corresponding 
   * to the given names.
   * @throws IllegalArgumentException if an invalid column name is provided.
   */
  public ColumnSet excludes(String...names) throws IllegalArgumentException;
  
  /**
   * @param names a {@link List} of column names.
   * @return the {@link ColumnSet} corresponding to the specified subset.
   * @throws IllegalArgumentException if an invalid column name is provided.
   */
  public ColumnSet excludes(List<String> names) throws IllegalArgumentException;
  
  /**
   * @param names the names of the columns to check for.
   * @return <code>true</code> if this instance has the columns corresponding to the
   * given names, in the specified order.
   */
  public boolean contains(String...names);
  
  /**
   * @param names the names of the columns to check for.
   * @return <code>true</code> if this instance has the columns corresponding to the
   * given names, in the specified order.
   */
  public boolean contains(List<String> names);
  
  /**
   * @return the {@link List} of this instance's column names.
   */
  public List<String> getColumnNames();
  
  /**
   * @return a copy of this instance, but with the column indices reset to reflect their real order,
   * independently of the original dataset from which this instance comes.
   */
  public ColumnSet detach();
  
}

