package org.sapia.domain.dublincore;


/**
 *
 *
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */
public class Rights extends AbstractDublinCoreObject {
  /**
   * Creates a new Rights instance with the value passed in.
   *
   * @param aValue The value of this rights.
   */
  public Rights(String aValue) {
    super(aValue);
  }

  /**
   * Creates a new Rights instance with the parameters passed in.
   *
   * @param aValue The value of this rights.
   * @param aLanguageCode The language code of this rights.
   */
  public Rights(String aValue, String aLanguageCode) {
    super(aValue, aLanguageCode);
  }
}
