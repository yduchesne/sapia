package org.sapia.dataset;

import java.util.Comparator;

import org.sapia.dataset.type.DateDatatypeStrategy;
import org.sapia.dataset.type.GeometryDatatypeStrategy;
import org.sapia.dataset.type.NumericDatatypeStrategy;
import org.sapia.dataset.type.StringDatatypeStrategy;

public enum Datatype {

  STRING(new StringDatatypeStrategy()),
  NUMERIC(new NumericDatatypeStrategy()),
  DATE(new DateDatatypeStrategy()),
  GEOMETRY(new GeometryDatatypeStrategy());
  
  private DatatypeStrategy strategy;
  private Comparator<Object> comparator;
  
  private Datatype(final DatatypeStrategy strategy) {
    this.strategy   = strategy;
    this.comparator = new Comparator<Object>() {
      @Override
      public int compare(Object o1, Object o2) {
        return strategy.compareTo(o1, o2);
      }
    };
  }
  
  /**
   * @return this instance's {@link DateDatatypeStrategy}.
   */
  public DatatypeStrategy strategy() {
    return strategy;
  }
  
  /**
   * @see DatatypeStrategy#compareTo(Object, Object)
   * @return this instance's comparator.
   */
  public Comparator<Object> comparator() {
    return comparator;
  }
  
}
