package org.sapia.dataset.format;

import java.text.DecimalFormat;

import org.sapia.dataset.Datatype;
import org.sapia.dataset.value.Values;

/**
 * Holds formatting configuration.
 * 
 * @author yduchesne
 */
public class DefaultFormat implements Format {

  private static final double MAX_DOUBLE = 9999999.9999;
  
  private static final DecimalFormat STD_DECIMAL_FORMAT = new DecimalFormat("#######.####");
  
  private static final DecimalFormat EXT_DECIMAL_FORMAT = new DecimalFormat("0.###E0");
  
  
  @Override
  public String formatHeader(String colName) {
    return colName;
  }
  
  @Override
  public String formatValue(Datatype datatype, Object value) {
    if (value instanceof SelfFormattable) {
      return ((SelfFormattable) value).format(datatype, this);
    } else if (datatype == Datatype.NUMERIC) {
      synchronized (STD_DECIMAL_FORMAT) {
        double doubleVal = Values.doubleValue(value);
        if (doubleVal <= MAX_DOUBLE) {
          return STD_DECIMAL_FORMAT.format(doubleVal);
        } else {
          return EXT_DECIMAL_FORMAT.format(doubleVal);
        }
      }
    } else {
      return String.format("%s", value);
    }
  }
  
  /**
   * @return a new {@link DefaultFormat}.
   */
  public static final DefaultFormat getInstance() {
    return new DefaultFormat();
  }
}
