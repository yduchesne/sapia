package org.sapia.dataset.type;

import java.util.Date;

import org.sapia.dataset.Datatype;
import org.sapia.dataset.DatatypeStrategy;
import org.sapia.dataset.util.Checks;
import org.sapia.dataset.value.NullValue;

public class NumericDatatypeStrategy implements DatatypeStrategy {
  
  public boolean isAssignableFrom(Object value) {
    return value instanceof Number || NullValue.isNull(value);
  }
  
  @Override
  public Object add(Object currentValue, Object toAdd) {
    if (NullValue.isNull(toAdd)) {
      return currentValue;
    } else if (NullValue.isNull(currentValue)) {
      if (isAssignableFrom(toAdd)) {
        return ((Number) toAdd).doubleValue();
      } else if (Datatype.STRING.strategy().isAssignableFrom(toAdd)) {
        String toAddStr = (String) toAdd;
        try {
          Double toAddDouble = Double.parseDouble(toAddStr);
          return toAddDouble; 
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException(String.format("Cannot add %s to %s", toAddStr, currentValue));
        }
      } else if (Datatype.DATE.strategy().isAssignableFrom(toAdd)) {
        Date toAddDate = (Date) toAdd;
        return toAddDate.getTime();
      } else {
        throw new IllegalArgumentException(String.format("Cannot convert %s to type %s", toAdd, Datatype.NUMERIC));
      }
    } else if (isAssignableFrom(toAdd)) {
      return new Double(((Number) currentValue).doubleValue() + ((Number) toAdd).doubleValue());
    } else if (Datatype.STRING.strategy().isAssignableFrom(toAdd)) {
      String toAddStr = (String) toAdd;
      try {
        Double toAddDouble = Double.parseDouble(toAddStr);
        return new Double(((Number) currentValue).doubleValue() + toAddDouble);        
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(String.format("Cannot add %s to %s", toAddStr, currentValue));
      }
    } else if (Datatype.DATE.strategy().isAssignableFrom(toAdd)) {
      Date toAddDate = (Date) toAdd;
      return new Double(((Number) currentValue).doubleValue() + toAddDate.getTime());
    } else {
      throw new IllegalArgumentException(String.format("Cannot add %s to %s", toAdd, currentValue));
    }
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
