package org.sapia.domain.dublincore;


/**
 *
 *
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */
public class Type extends AbstractDublinCoreObject {
  /**
   * Creates a new Type instance with the value passed in.
   *
   * @param aValue The value of this type.
   */
  public Type(String aValue) {
    super(aValue);
  }

  /**
   * Creates a new Type instance with the parameters passed in.
   *
   * @param aValue The value of this type.
   * @param aLanguageCode The language code of this type.
   */
  public Type(String aValue, String aLanguageCode) {
    super(aValue, aLanguageCode);
  }
}
