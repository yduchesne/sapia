package org.sapia.domain.dublincore;


/**
 *
 *
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */
public class Publisher extends AbstractDublinCoreObject {
  /**
   * Creates a new Publisher instance with the value passed in.
   *
   * @param aValue The value of this publisher.
   */
  public Publisher(String aValue) {
    super(aValue);
  }

  /**
   * Creates a new Publisher instance with the parameters passed in.
   *
   * @param aValue The value of this publisher.
   * @param aLanguageCode The language code of this publisher.
   */
  public Publisher(String aValue, String aLanguageCode) {
    super(aValue, aLanguageCode);
  }
}
