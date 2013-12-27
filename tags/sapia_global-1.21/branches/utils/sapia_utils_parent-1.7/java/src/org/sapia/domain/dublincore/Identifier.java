package org.sapia.domain.dublincore;


/**
 *
 *
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */
public class Identifier extends AbstractDublinCoreObject {
  /**
   * Creates a new Identifier instance with the value passed in.
   *
   * @param aValue The value of this identifier.
   */
  public Identifier(String aValue) {
    super(aValue);
  }

  /**
   * Creates a new Identifier instance with the parameters passed in.
   *
   * @param aValue The value of this identifier.
   * @param aLanguageCode The language code of this identifier.
   */
  public Identifier(String aValue, String aLanguageCode) {
    super(aValue, aLanguageCode);
  }
}
