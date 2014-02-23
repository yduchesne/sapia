package org.sapia.dataset.type;

import org.sapia.dataset.DatatypeStrategy;
import org.sapia.dataset.util.Checks;
import org.sapia.dataset.value.NullValue;

public class NumericDatatypeStrategy implements DatatypeStrategy {
  
  public boolean isAssignableFrom(Object value) {
    return value instanceof Number || NullValue.isNull(value);
  }

  public int compareTo(Object value, Object operand) { 
    Checks.isTrue(isAssignableFrom(operand) || NullValue.isNull(operand), "Number %s cannot be compared with %s", value, operand);
    if (NullValue.isNull(value) && NullValue.isNull(operand)) {
      return 0;
    } else if (NullValue.isNull(value)) {
      return -1;
    } else if (NullValue.isNull(operand)) {
      return 1;
    } else {
      Number numValue =   (Number) value;
      Number numOperand = (Number) operand;
      if (numValue.doubleValue() < numOperand.doubleValue()) {
        return -1;
      } else if (numValue.doubleValue() > numOperand.doubleValue()) {
        return 1;
      } else {
        return 0;
      }
    }
  }
}
