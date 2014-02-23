package org.sapia.dataset.parser;

import org.sapia.dataset.util.Strings;

/**
 * Parses numeric content.
 * 
 * @author yduchesne
 *
 */
public class NumericParser implements Parser {
  
  @Override
  public Object parse(String content) {
    if (Strings.isNullOrEmpty(content)) {
      return 0d;
    }
    try {
      return Double.parseDouble(content);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(String.format("Could not parse value: %s", content));
    }
  }
  
}
