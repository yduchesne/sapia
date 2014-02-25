package org.sapia.dataset.type;

import java.util.Date;

import org.sapia.dataset.Datatype;
import org.sapia.dataset.DatatypeStrategy;
import org.sapia.dataset.parser.DateParser;
import org.sapia.dataset.util.Checks;
import org.sapia.dataset.value.NullValue;

public class DateDatatypeStrategy implements DatatypeStrategy {
  
  private static DateParser PARSER = new DateParser();
  
  @Override
  public boolean isAssignableFrom(Object value) {
    return value instanceof Date || NullValue.isNull(value);
  }
  
  @Override
  public Object add(Object currentValue, Object toAdd) {
    if (NullValue.isNull(toAdd)) {
      return currentValue;
    } else if (NullValue.isNull(currentValue)) {
      if (isAssignableFrom(toAdd)) {
        return ((Date) toAdd).getTime();
      } else if (Datatype.STRING.strategy().isAssignableFrom(toAdd)) {
        String toAddStr = (String) toAdd;
        Date toAddDate = (Date) PARSER.parse(toAddStr);
        return toAddDate;
      } else if (Datatype.NUMERIC.strategy().isAssignableFrom(toAdd)) {
        Number toAddNumber = (Number) toAdd;
        return toAddNumber;
      } else {
        throw new IllegalArgumentException(String.format("Cannot convert %s to type %s", toAdd, Datatype.NUMERIC));
      }
    } else if (isAssignableFrom(toAdd)) {
      return new Date(((Date) currentValue).getTime() + ((Date) toAdd).getTime());
    } else if (Datatype.STRING.strategy().isAssignableFrom(toAdd)) {
      String toAddStr = (String) toAdd;
      Date toAddDate = (Date) PARSER.parse(toAddStr);
      return new Date(((Date) currentValue).getTime() + toAddDate.getTime());
    } else if (Datatype.NUMERIC.strategy().isAssignableFrom(toAdd)) {
      Number toAddNumber = (Number) toAdd;
      return new Date(((Date) currentValue).getTime() + toAddNumber.longValue());
    } else {
      throw new IllegalArgumentException(String.format("Cannot add %s to %s", toAdd, currentValue));
    }
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
