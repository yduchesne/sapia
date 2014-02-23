package org.sapia.dataset;

import java.util.List;

import org.sapia.dataset.algo.Criteria;

public interface Dataset extends Iterable<Vector> {

  /**
   * @return this instance's {@link ColumnSet}.
   */
  public ColumnSet getColumnSet();
  
  /**
   * @return the number of rowns in this dataset.
   */
  public int size();
  
  /**
   * @param rowIndex the index of the row to return.
   * @return the {@link Vector} at the given row index.
   * @throws IllegalArgumentException if the given index is invalid.
   */
  public Vector getRow(int rowIndex) throws IllegalArgumentException;
  
  /**
   * @param colIndex the index of the column to return.
   * @return the {@link Vector} corresponding to the given column index.
   * @throws IllegalArgumentException if the given index is invalid.
   */
  public Vector getColumn(int colIndex) throws IllegalArgumentException;

  /**
   * @param colIndex the index of the column to return.
   * @return the {@link Vector} corresponding to the given column index.
   * @throws IllegalArgumentException if the given index is invalid.
   */
  public Vector getColumn(String name) throws IllegalArgumentException;
  
  /**
   * @param colName a column name.
   * @param filter the {@link Criteria} to use as a filter.
   * @return the {@link Dataset} corresponding to the subset of rows in the 
   * given column that are matched by the provided criteria.
   * @throws IllegalArgumentExcepwtion if the column name is invalid.
   */
  public Dataset getColumnSubset(String colName, Criteria<Object> filter) throws IllegalArgumentException;

  /**
   * @param colIndex a column index.
   * @param filter the {@link Criteria} to use as a filter.
   * @return the {@link Dataset} corresponding to the subset of rows in the 
   * given column that are matched by the provided criteria.
   * @throws IllegalArgumentException if the column index is invalid.
   */
  public Dataset getColumnSubset(int colIndex, Criteria<Object> filter) throws IllegalArgumentException;
  
  /**
   * @param filter the {@link Criteria} to use as a filter.
   * @return a {@link Dataset} corresponding to the subset of this instance's rows
   * that are matched by the given criteria.
   */
  public Dataset getSubset(Criteria<RowResult> filter);
  
  /**
   * @param colNames one or more column names on which to create an index.
   * @return the {@link IndexedDataset} encapsulating an index view of this instance's data.
   * 
   * @throws IllegalArgumentException if the anyone of the given column names is invalid.
   */
  public IndexedDataset index(String...colNames) throws IllegalArgumentException;
  
  /**
   * @param colNames one or more column names on which to create an index.
   * @return the {@link IndexedDataset} encapsulating an index view of this instance's data.
   * 
   * @throws IllegalArgumentException if the anyone of the given column names is invalid.
   */
  public IndexedDataset index(List<String> colNames) throws IllegalArgumentException;
  
  /**
   * @return the Dataset's head.
   */
  public Dataset head();
  
  /**
   * @return the Dataset's tail.
   */
  public Dataset tail();
}
