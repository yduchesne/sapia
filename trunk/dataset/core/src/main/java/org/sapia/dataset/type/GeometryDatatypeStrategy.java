package org.sapia.dataset.type;

import org.sapia.dataset.DatatypeStrategy;

public class GeometryDatatypeStrategy implements DatatypeStrategy {
  
  @Override
  public boolean isAssignableFrom(Object value) {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public int compareTo(Object value, Object operand) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object add(Object currentValue, Object toAdd) {
    throw new UnsupportedOperationException();
  }
}
