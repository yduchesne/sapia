package org.sapia.dataset.type;

import org.sapia.dataset.DatatypeStrategy;
import org.sapia.dataset.util.Checks;
import org.sapia.dataset.value.NullValue;

public class StringDatatypeStrategy implements DatatypeStrategy {
 
  public StringDatatypeStrategy() {
  }
  
  @Override
  public boolean isAssignableFrom(Object value) {
    return value instanceof String || NullValue.isNull(value);
  }

  @Override
  public int compareTo(Object value, Object operand) {
    Checks.isTrue(isAssignableFrom(operand) || NullValue.isNull(operand), "String %s cannot be compared with %s", value, operand);
    if (NullValue.isNull(value) && NullValue.isNull(operand)) {
      return 0;
    } else if (NullValue.isNull(value)) {
      return -1;
    } else if (NullValue.isNull(operand)) {
      return 1;
    } else {
      String strValue =   (String) value;
      String strOperand = (String) operand;
      return strValue.compareTo(strOperand);
    } 
  }
}
