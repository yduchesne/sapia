package org.sapia.dataset.stat;

import org.sapia.dataset.value.MutableValue;

/**
 * Overrides the {@link MutableValue} by allowing the setting of a new value 
 * only if it is greater than the current one.
 * 
 * @author yduchesne
 *
 */
public class MaxValue extends MutableValue {

  @Override
  public void set(double value) {
    if (value > super.get()) {
      super.set(value);
    }
  }
}
