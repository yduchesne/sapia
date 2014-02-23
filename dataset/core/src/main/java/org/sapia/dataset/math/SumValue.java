package org.sapia.dataset.math;

import org.sapia.dataset.value.MutableValue;

public class SumValue extends MutableValue {
  
  /**
   * @param value adds the given value to this instance.
   * @return this instance.
   */
  public SumValue add(double value) {
    super.set(get() + value);
    return this;
  }

}
