package org.sapia.dataset.util;

import java.util.concurrent.TimeUnit;

/**
 * 
 * @author yduchesne
 *
 */
public class Time {

  private long      value;
  private TimeUnit  unit;
  
  /**
   * @param value a time value.
   * @param unit the {@link TimeUnit} in which the given value is expressed.
   */
  public Time(long value, TimeUnit unit) {
    this.value = value;
    this.unit  = unit;
  }
  
  /**
   * @return the {@link TimeUnit} in which this instance is expressed.
   */
  public TimeUnit getUnit() {
    return unit;
  }
  
  /**
   * @return this instance's value.
   */
  public long getValue() {
    return value;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Time) {
      Time other = (Time) obj;
      return other.getUnit() == unit 
          && other.value == value;
    }
    return false;
  }
  
  @Override
  public int hashCode() {
    return Objects.safeHashCode(value, unit);
  }
  
  @Override
  public String toString() {
    return Strings.toString("value", value, "unit", unit);
  }
}
