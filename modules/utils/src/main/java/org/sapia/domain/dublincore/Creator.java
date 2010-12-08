package org.sapia.domain.dublincore;


/**
 *
 *
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */
public class Creator extends AbstractDublinCoreObject {
  /**
   * Creates a new Creator instance with the value passed in.
   *
   * @param aValue The value of this creator.
   */
  public Creator(String aValue) {
    super(aValue);
  }

  /**
   * Creates a new Creator instance with the parameters passed in.
   *
   * @param aValue The value of this creator.
   * @param aLanguageCode The language code of this creator.
   */
  public Creator(String aValue, String aLanguageCode) {
    super(aValue, aLanguageCode);
  }
}
