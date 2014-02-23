package org.sapia.dataset.parser;

import org.sapia.dataset.Vector;

/**
 * Specifies the behavior for converting {@link Vector} values out of string content.
 * 
 * @author yduchesne
 *
 */
public interface Parser {

  /**
   * @param content some content.
   * @return the parsed content.
   */
  public Object parse(String content);
}
