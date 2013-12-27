package org.sapia.domain.dublincore;


/**
 *
 *
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */
public class Format extends AbstractDublinCoreObject {
  /**
   * Creates a new Format instance with the value passed in.
   *
   * @param aValue The value of this format.
   */
  public Format(String aValue) {
    super(aValue);
  }

  /**
   * Creates a new Format instance with the parameters passed in.
   *
   * @param aValue The value of this format.
   * @param aLanguageCode The language code of this format.
   */
  public Format(String aValue, String aLanguageCode) {
    super(aValue, aLanguageCode);
  }
}
