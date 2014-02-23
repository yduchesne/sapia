package org.sapia.dataset.parser;

import java.text.DateFormat;
import java.text.ParseException;

import org.sapia.dataset.conf.Conf;

/**
 * Parses date content.
 * 
 * @author yduchesne
 *
 */
public class DateParser implements Parser {
  
  @Override
  public Object parse(String content) {
    for (DateFormat format : Conf.getDateFormats()) {
      synchronized (format) {
        try {
          return format.parse(content);
        } catch (ParseException e) {
          // noop: trying next
        }
      }
    }
    throw new IllegalArgumentException("Could not parse date: " + content);
  }

}
