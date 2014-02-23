package org.sapia.dataset.stat;

import org.sapia.dataset.value.MutableValue;

/**
 * Overrides the {@link MutableValue} by allowing the setting of a new value 
 * only if it is lower than the current one.
 * 
 * @author yduchesne
 *
 */
public class MinValue extends MutableValue {
  
  private boolean isSet;
  
  public MinValue() {
  }
  
  @Override
  public void set(double value) {
    if (!isSet || value < super.get()) {
      super.set(value);
      isSet = true;
    }
  }

}
