package org.sapia.domain.dublincore;


/**
 *
 *
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */
public class Date extends AbstractDublinCoreObject {
  /**
   * Creates a new Date instance with the value passed in.
   *
   * @param aValue The value of this date.
   */
  public Date(String aValue) {
    super(aValue);
  }

  /**
   * Creates a new Date instance with the parameters passed in.
   *
   * @param aValue The value of this date.
   * @param aLanguageCode The language code of this date.
   */
  public Date(String aValue, String aLanguageCode) {
    super(aValue, aLanguageCode);
  }
}
