package org.sapia.dataset.io.helpers;

import org.sapia.dataset.Datatype;
import org.sapia.dataset.parser.DateParser;
import org.sapia.dataset.parser.NumericParser;
import org.sapia.dataset.parser.Parser;
import org.sapia.dataset.parser.WktParser;

/**
 * A utility class that can be used to attempt determining the data type of
 * dataset file columns.
 * 
 * @author yduchesne
 *
 */
public class ColumnTypeGuesser {
  
  private DateParser    dateParser    = new DateParser();
  private NumericParser numericParser = new NumericParser();
  private WktParser     wktParser     = new WktParser();
  
  /**
   * @param line a content {@link Line}.
   * @return the array of {@link Datatype}s corresponding to the different columns
   * in the given line, or <code>null</code> if the column data types could not be determined.
   */
  public Datatype[] guessColumnType(Line line) {
    Datatype[] types = new Datatype[line.length()];
    for (int i = 0; i < line.length(); i++) {
      String value = line.get(i);
      if (value != null && value.trim().length() > 0) {
        if (tryParse(dateParser, value)) {
          types[i] = Datatype.DATE;
        } else if (tryParse(numericParser, value)) {
          types[i] = Datatype.NUMERIC;
        } else if (tryParse(wktParser, value)) {
          types[i] = Datatype.GEOMETRY;
        } else {
          types[i] = Datatype.STRING;
        }
      } else {
        return null;
      }
    }
    return types;
  }
  
  private boolean tryParse(Parser parser, String value) {
    try {
      parser.parse(value);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
