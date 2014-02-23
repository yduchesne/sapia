package org.sapia.dataset.impl;

import java.util.Iterator;
import java.util.List;

import org.sapia.dataset.RowSet;
import org.sapia.dataset.Vector;
import org.sapia.dataset.util.Checks;

/**
 * An implementation of the {@link RowSet} interfaces (an instance of this class
 * wraps a {@link List} of {@link Vector}s).
 * 
 * @author yduchesne
 *
 */
public class DefaultRowSet implements RowSet {
  
  private List<Vector> rows;
  
  public DefaultRowSet(List<Vector> rows) {
    this.rows = rows;
  }
  
  @Override
  public Vector get(int index) throws IllegalArgumentException {
    Checks.bounds(index, rows, "Invalid index: %s. Got %s rows", index, rows.size());
    return rows.get(index);
  }
  
  @Override
  public int size() {
    return rows.size();
  }
  
  @Override
  public Iterator<Vector> iterator() {
    return new ImmutableIterator<>(rows.iterator());
  }
}
