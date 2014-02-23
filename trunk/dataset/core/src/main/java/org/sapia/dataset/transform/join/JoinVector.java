package org.sapia.dataset.transform.join;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.sapia.dataset.Vector;
import org.sapia.dataset.impl.DefaultVector;
import org.sapia.dataset.transform.join.VectorTable.VectorType;

/**
 * Implements the {@link Vector} interface over two other vectors.
 * 
 * @author yduchesne
 *
 */
class JoinVector implements Vector {

  private VectorTable  table;
  private Vector left, right;
  private int totalSize;
  
  JoinVector(VectorTable table, Vector left, Vector right) {
    this.table = table;
    this.left  = left;
    this.right = right;
    totalSize = left.size() + right.size();
  }
  
  @Override
  public Object get(int index) throws IllegalArgumentException {
    if (table.resolveVectorType(index) == VectorType.LEFT) {
      return left.get(table.resolveVectorIndex(index));
    } else {
      return right.get(table.resolveVectorIndex(index));
    }
  }
  
  @Override
  public Iterator<Object> iterator() {
    return new Iterator<Object>() {
      private int index;
      @Override
      public boolean hasNext() {
        return index < totalSize;
      }
      
      @Override
      public Object next() {
        if (index >= totalSize) {
          throw new NoSuchElementException();
        }
        return get(index++);
      }
      
      @Override
      public void remove() {
      }
    };
  }
  
  @Override
  public int size() {
    return totalSize;
  }
  
  @Override
  public Vector subset(int... indices) throws IllegalArgumentException {
    Object[] values = new Object[indices.length];
    for (int i = 0; i < indices.length; i++) {
      int index = indices[i];
      Object value = get(index);
      values[i] = value;
    }
    return new DefaultVector(values);
  }

  @Override
  public Object[] toArray() {
    Object[] values = new Object[totalSize];
    for (int i = 0; i < totalSize; i++) {
      Object value = get(i);
      values[i] = value;
    }
    return values;
  }
}
