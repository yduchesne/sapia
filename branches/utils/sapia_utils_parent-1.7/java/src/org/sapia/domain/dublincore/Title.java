package org.sapia.domain.dublincore;


/**
 *
 *
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */
public class Title extends AbstractDublinCoreObject {
  /**
   * Creates a new Title instance with the value passed in.
   *
   * @param aValue The value of this title.
   */
  public Title(String aValue) {
    super(aValue);
  }

  /**
   * Creates a new Title instance with the parameters passed in.
   *
   * @param aValue The value of this title.
   * @param aLanguageCode The language code of this title.
   */
  public Title(String aValue, String aLanguageCode) {
    super(aValue, aLanguageCode);
  }
}
