package org.sapia.dataset.parser;


/**
 * Returns the given content.
 * 
 * @author yduchesne
 *
 */
public class StringParser implements Parser {
  
  @Override
  public Object parse(String content) {
    return content;
  }

}
