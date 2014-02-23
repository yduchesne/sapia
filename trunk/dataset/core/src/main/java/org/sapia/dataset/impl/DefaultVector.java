package org.sapia.dataset.impl;

import java.util.Iterator;
import java.util.List;

import org.sapia.dataset.Vector;
import org.sapia.dataset.util.Objects;

/**
 * Default imlementation of the {@link Vector} interface.
 * 
 * @author yduchesne
 *
 */
public class DefaultVector implements Vector {
  
  private Object[] values;
  
  /**
   * @param values the array of values to wrap.
   */
  public DefaultVector(Object...values) {
    this.values = values;
  }
  public DefaultVector(List<Object> values) {
    this(values.toArray(new Object[values.size()]));
  }
  
  @Override
  public Object get(int index) throws IllegalArgumentException {
    if (index < 0 || index >= values.length) {
      throw new IllegalArgumentException(String.format("Invalid index: %s. Got %s values", index, values.length));
    }
    return values[index];
  }
  
  @Override
  public Vector subset(int...indices) throws IllegalArgumentException {
    Object[] values = new Object[indices.length];
    for (int i = 0; i < indices.length; i++) {
      int index = indices[i];
      Object value = get(index);
      values[i] = value;
    }
    return new DefaultVector(values);
  }
  
  @Override
  public int size() {
    return values.length;
  }
  
  @Override
  public Iterator<Object> iterator() {
    return new Iterator<Object>() {
      private int count;
      @Override
      public boolean hasNext() {
        return count < values.length;
      }
      
      @Override
      public Object next() {
        return values[count++];
      }
      
      @Override
      public void remove() {
      }
    };
  }
  
  @Override
  public Object[] toArray() {
    Object[] toReturn = new Object[values.length];
    System.arraycopy(values, 0, toReturn, 0, values.length);
    return toReturn;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Vector) {
      Vector other = (Vector) obj;
      if (values.length != other.size()) {
        return false;
      }
      for (int i = 0; i < values.length; i++) {
        if (!Objects.safeEquals(values[i], other.get(i))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  @Override
  public int hashCode() {
    return Objects.safeHashCode(values);
  }
  
  @Override
  public String toString() {
    StringBuilder s = new StringBuilder("[");
    for (int i = 0; i < values.length; i++) {
      if (i > 0) {
        s.append(",");
      }
      s.append(values[i]);
    }
    return s.append("]").toString();
  }
}
