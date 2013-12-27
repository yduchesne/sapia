package org.sapia.domain.dublincore;


/**
 *
 *
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */
public class Coverage extends AbstractDublinCoreObject {
  /**
   * Creates a new Coverage instance with the value passed in.
   *
   * @param aValue The value of this coverage.
   */
  public Coverage(String aValue) {
    super(aValue);
  }

  /**
   * Creates a new Coverage instance with the parameters passed in.
   *
   * @param aValue The value of this coverage.
   * @param aLanguageCode The language code of this coverage.
   */
  public Coverage(String aValue, String aLanguageCode) {
    super(aValue, aLanguageCode);
  }
}
