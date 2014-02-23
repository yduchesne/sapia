package org.sapia.dataset.impl;

import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.RowResult;
import org.sapia.dataset.Vector;

/**
 * Implements the {@link RowResult} interface by wrapping a {@link ColumnSet} and
 * a {@link Vector}.
 * <p>
 * An instance of this class is not meant to be shared among multiple threads: its state
 * is mutable by allowing to set the {@link Vector} instance that it wraps.
 * 
 * @author yduchesne
 *
 */
public class DefaultRowResult implements RowResult {
  
  private ColumnSet columns;
  private Vector    vector;
  
  /**
   * @param columns a {@link ColumnSet}.
   */
  public DefaultRowResult(ColumnSet columns) {
    this.columns = columns;
  }
  
  /**
   * @param vector a {@link Vector}.
   */
  public void setVector(Vector vector) {
    this.vector = vector;
  }
  
  /**
   * @return this instance's {@link Vector}.
   */
  public Vector getVector() {
    return vector;
  }
  
  @Override
  public Object get(int index) throws IllegalArgumentException {
    return vector.get(index);
  }
  
  @Override
  public Object get(String name) throws IllegalArgumentException {
    return vector.get(columns.get(name).getIndex());
  }
  
}
