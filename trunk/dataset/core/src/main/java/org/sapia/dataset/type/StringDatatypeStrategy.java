package org.sapia.dataset.type;

import java.util.Date;

import org.sapia.dataset.Datatype;
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
  public Object add(Object currentValue, Object toAdd) {
    if (NullValue.isNull(toAdd)) {
      return currentValue;
    } else if (NullValue.isNull(currentValue)) {
      if (isAssignableFrom(toAdd)) {
        return (String) toAdd ;
      } else if (Datatype.NUMERIC.strategy().isAssignableFrom(toAdd)) {
        return ((Number) toAdd).toString() ;
      } else if (Datatype.DATE.strategy().isAssignableFrom(toAdd)) {
        String toAddStr = ((Date) toAdd).toString();
        return toAddStr;
      } else {
        throw new IllegalArgumentException(String.format("Cannot convert %s to type %s", toAdd, Datatype.NUMERIC));
      } 
    } else if (isAssignableFrom(toAdd)) {
      return ((String) currentValue) + ((String) toAdd) ;
    } else if (Datatype.NUMERIC.strategy().isAssignableFrom(toAdd)) {
      return ((String) currentValue) + ((Number) toAdd).toString() ;
    } else if (Datatype.DATE.strategy().isAssignableFrom(toAdd)) {
      String toAddStr = ((Date) toAdd).toString();
      return ((String) currentValue) + toAddStr;
    } else {
      throw new IllegalArgumentException(String.format("Cannot add %s to %s", toAdd, currentValue));
    }
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
