package org.sapia.dataset.transform.view;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.swing.text.View;

import org.sapia.dataset.Vector;
import org.sapia.dataset.impl.DefaultVector;

/**
 * An instance of this class wraps another vector, only "showing" values 
 * of that other vector that correspond to the columns selected as part of the
 * view.
 * 
 * @see View
 * @see ViewDataset
 * 
 * @author yduchesne
 *
 */
class ViewVector implements Vector {
  
  private int[]  columnIndices;
  private Vector delegate;
  
  ViewVector(int[] columnIndices, Vector delegate) {
    this.columnIndices = columnIndices;
    this.delegate = delegate;
  }
  
  @Override
  public Object get(int index) throws IllegalArgumentException {
    int realIndex = columnIndices[index];
    return delegate.get(realIndex);
  }
  
  @Override
  public int size() {
    return columnIndices.length;
  }
  
  @Override
  public Iterator<Object> iterator() {
    return new Iterator<Object>() {
      private int index;
      @Override
      public boolean hasNext() {
        return index < columnIndices.length;
      }
      @Override
      public Object next() {
        if (index >= columnIndices.length) {
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
  public Vector subset(int... indices) throws IllegalArgumentException {
    Object[] values = new Object[indices.length];
    for (int i = 0; i < indices.length; i++) {
      values[i] = get(indices[i]);
    }
    return new DefaultVector(values);
  }
  
  @Override
  public Object[] toArray() {
    Object[] values = new Object[columnIndices.length];
    for (int i = 0; i < columnIndices.length; i++) {
      values[i] = get(i);
    }
    return values;
  }

}
