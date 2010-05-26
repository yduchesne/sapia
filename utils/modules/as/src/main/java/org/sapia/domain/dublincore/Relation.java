package org.sapia.domain.dublincore;


/**
 *
 *
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */
public class Relation extends AbstractDublinCoreObject {
  /**
   * Creates a new Relation instance with the value passed in.
   *
   * @param aValue The value of this relation.
   */
  public Relation(String aValue) {
    super(aValue);
  }

  /**
   * Creates a new Relation instance with the parameters passed in.
   *
   * @param aValue The value of this relation.
   * @param aLanguageCode The language code of this relation.
   */
  public Relation(String aValue, String aLanguageCode) {
    super(aValue, aLanguageCode);
  }
}
