package org.sapia.domain.dublincore;


/**
 *
 *
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */
public class Language extends AbstractDublinCoreObject {
  /**
   * Creates a new Language instance with the value passed in.
   *
   * @param aValue The value of this language.
   */
  public Language(String aValue) {
    super(aValue);
  }

  /**
   * Creates a new Language instance with the parameters passed in.
   *
   * @param aValue The value of this language.
   * @param aLanguageCode The language code of this language.
   */
  public Language(String aValue, String aLanguageCode) {
    super(aValue, aLanguageCode);
  }
}
