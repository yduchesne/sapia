package org.sapia.dataset.format;

import org.sapia.dataset.Datatype;
import org.sapia.dataset.Vector;

/**
 * Abstracts formatting of {@link Vector} values.
 * 
 * @author yduchesne
 *
 */
public interface Format {
  
  /**
   * @param colName
   * @return a formatter column header.
   */
  public String formatHeader(String colName);
  
  /**
   * @param datatype the {@link Datatype} of the the given value.
   * @param value a value to format.
   * @return the formatted {@link String}.
   */
  public String formatValue(Datatype datatype, Object value);

}
