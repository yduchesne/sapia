package org.sapia.dataset.io.weka;

import java.util.Date;
import java.util.Iterator;

import org.sapia.dataset.Vector;
import org.sapia.dataset.impl.DefaultVector;
import org.sapia.dataset.util.Checks;
import org.sapia.dataset.util.Objects;
import org.sapia.dataset.value.NullValue;

import weka.core.Attribute;
import weka.core.Instance;

/**
 * Implements a {@link Vector} over the Weka {@link Instance} interface.
 * 
 * @author yduchesne
 */
public class WekaVectorAdapter implements Vector {
  
  private Instance instance;
  private int      hashCode  = -1;
  
  /**
   * @param instance the {@link Instance} to wrap.
   */
  public WekaVectorAdapter(Instance instance) {
    this.instance = instance;
  }
  
  @Override
  public Object get(int index) throws IllegalArgumentException {
    return doGet(index);
  }
  
  @Override
  public int size() {
    return instance.numAttributes();
  }
  
  @Override
  public Vector subset(int... indices) throws IllegalArgumentException {
    Object[] values = new Object[indices.length];
    for (int i = 0; i < indices.length; i++) {
      values[i] = doGet(indices[i]); 
    }
    return new DefaultVector(values);
  }
  
  private Object doGet(int index) {
    Checks.isFalse(index >= instance.numAttributes(), "Invalid index: %s. Got %s values", index, instance.numAttributes());
    Attribute attr = instance.attribute(index);
    Object value;
    if (instance.isMissing(attr.index())) {
      value = NullValue.getInstance();
    } else if (attr.isNominal()) {
      int realIndex = (int) instance.value(index);
      value = attr.value(realIndex);
    } else {
      value = instance.value(attr.index());
    }
    if (!NullValue.isNull(value) && attr.type() == Attribute.DATE) {
      value = new Date(((Double) value).longValue());
    } 
    return value;
  }
  
  @Override
  public Iterator<Object> iterator() {
    return new Iterator<Object>() {
      private int index = 0;
      @Override
      public boolean hasNext() {
        return index < instance.numAttributes();
      }
      
      @Override
      public Object next() {
        return doGet(index++);
      }
      
      @Override
      public void remove() {
      }
    };
  }
  
  @Override
  public Object[] toArray() {
    Object[] toReturn = new Object[instance.numAttributes()];
    for (int i = 0; i < toReturn.length; i++) {
      toReturn[i] = get(i);
    }
    return toReturn;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Vector) {
      Vector other = (Vector) obj;
      if (instance.numAttributes() != other.size()) {
        return false;
      }
      for (int i = 0; i < instance.numAttributes(); i++) {
        Attribute attr = instance.attribute(i);
        if (!Objects.safeEquals(doGet(attr.index()), other.get(i))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  @Override
  public int hashCode() {
    if (hashCode < 0) {
      hashCode = Objects.safeHashCode(iterator());
    }
    return hashCode;
  }
  
  @Override
  public String toString() {
    StringBuilder s = new StringBuilder("[");
    for (int i = 0; i < instance.numAttributes(); i++) {
      if (i > 0) {
        s.append(",");
      }
      s.append(doGet(instance.attribute(i).index()));
    }
    return s.append("]").toString();
  }
}
