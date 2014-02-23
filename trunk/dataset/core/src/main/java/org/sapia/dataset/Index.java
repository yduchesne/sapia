package org.sapia.dataset;

import java.util.Collection;
import java.util.List;
import java.util.Set;


/**
 * An instance of this interface stores a {@link Dataset}'s data in an optimized
 * way, internally indexing the dataset's rows on given columns.
 * 
 * @author yduchesne
 *
 */
public interface Index {
  
  /**
   * @return this instance's {@link ColumnSet}, containing the {@link Column} 
   * instances that correspond to the underlying dataset's columns.
   */
  public ColumnSet getColumnSet();
  
  /**
   * @return the {@link ColumnSet} holding the {@link Column}s corresponding
   * to the columns on which this index is built.
   */
  public ColumnSet getIndexedColumnSet();

  /**
   * @return the {@link List} of {@link VectorKey}s.
   */
  public Collection<VectorKey> getKeys();
  
  /**
   * @param key a {@link VectorKey}.
   * @return the {@link RowSet} holding the {@link Vector}s corresponding
   * to the rows that match the given key.
   */
  public RowSet getRowSet(String[] columNames, Object[] values);
  
  /**
   * @param key a {@link VectorKey}.
   * @return the {@link RowSet} holding the {@link Vector}s corresponding
   * to the rows that match the given key.
   */
  public RowSet getRowset(VectorKey key);

  /**
   * @return the {@link RowSet} holding the {@link Vector}s corresponding
   * to this instance's rows.
   */
  public RowSet getRowSet();
  
  /**
   * @param columnName a column name.
   * @return the {@link Set} of {@link Object}s for the column.
   */
  public Set<Object> getValueSet(String columnName);
  
  /**
   * @param indices one or more {@link Index} instances with which to merge this instance's contents.
   * @return a new {@link Index}, holding the contents of this instance merged with the one of the given
   * indices.
   */
  public Index mergeWith(Index...indices);
  
  /**
   * @param indices one or more {@link Index} instances with which to merge this instance's contents.
   * @return a new {@link Index}, holding the contents of this instance merged with the one of the given
   * indices.
   */
  public Index mergeWith(List<Index> indices);
  
  /**
   * @return this instance's number of rows.
   */
  public int size();
  
}
