package org.sapia.domain.dublincore;


/**
 *
 *
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */
public class Contributor extends AbstractDublinCoreObject {
  /**
   * Creates a new Contributor instance with the value passed in.
   *
   * @param aValue The value of this contributor.
   */
  public Contributor(String aValue) {
    super(aValue);
  }

  /**
   * Creates a new Contributor instance with the parameters passed in.
   *
   * @param aValue The value of this contributor.
   * @param aLanguageCode The language code of this contributor.
   */
  public Contributor(String aValue, String aLanguageCode) {
    super(aValue, aLanguageCode);
  }
}
