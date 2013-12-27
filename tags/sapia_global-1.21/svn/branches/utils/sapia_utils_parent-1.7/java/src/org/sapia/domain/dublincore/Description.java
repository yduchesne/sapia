package org.sapia.domain.dublincore;


/**
 *
 *
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */
public class Description extends AbstractDublinCoreObject {
  /**
   * Creates a new Description instance with the value passed in.
   *
   * @param aValue The value of this description.
   */
  public Description(String aValue) {
    super(aValue);
  }

  /**
   * Creates a new Description instance with the parameters passed in.
   *
   * @param aValue The value of this description.
   * @param aLanguageCode The language code of this description.
   */
  public Description(String aValue, String aLanguageCode) {
    super(aValue, aLanguageCode);
  }
}
