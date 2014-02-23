package org.sapia.dataset.type;

import java.util.Date;

import org.sapia.dataset.DatatypeStrategy;
import org.sapia.dataset.util.Checks;
import org.sapia.dataset.value.NullValue;

public class DateDatatypeStrategy implements DatatypeStrategy {
  
  @Override
  public boolean isAssignableFrom(Object value) {
    return value instanceof Date || NullValue.isNull(value);
  }
  
  public int compareTo(Object value, Object operand) { 
    Checks.isTrue(isAssignableFrom(operand) || NullValue.isNull(operand), "Date %s cannot be compared with %s", value, operand);
    if (NullValue.isNull(value) && NullValue.isNull(operand)) {
      return 0;
    } else {
      Date dateValue = (Date) value;
      Date dateOperand = (Date) operand;
      return dateValue.compareTo(dateOperand);
    }
  }

}
