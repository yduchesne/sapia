package org.sapia.dataset.impl;

import java.util.Iterator;

import org.sapia.dataset.Dataset;
import org.sapia.dataset.RowSet;
import org.sapia.dataset.Vector;

/**
 * Adapts a {@link Dataset} to the {@link RowSet} interface.
 * 
 * @author yduchesne
 *
 */
public class DatasetRowSetAdapter implements RowSet {
  
  private Dataset dataset;
  
  /**
   * @param dataset the {@link Dataset} to wrap.
   */
  public DatasetRowSetAdapter(Dataset dataset) {
    this.dataset = dataset;
  }

  @Override
  public Vector get(int index) throws IllegalArgumentException {
    return dataset.getRow(index);
  }
  
  @Override
  public int size() {
    return dataset.size();
  }
  
  @Override
  public Iterator<Vector> iterator() {
    return dataset.iterator();
  }

}
