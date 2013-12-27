package org.sapia.domain.dublincore;


/**
 *
 *
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */
public class Subject extends AbstractDublinCoreObject {
  /**
   * Creates a new Subject instance with the value passed in.
   *
   * @param aValue The value of this subject.
   */
  public Subject(String aValue) {
    super(aValue);
  }

  /**
   * Creates a new Subject instance with the parameters passed in.
   *
   * @param aValue The value of this subject.
   * @param aLanguageCode The language code of this subject.
   */
  public Subject(String aValue, String aLanguageCode) {
    super(aValue, aLanguageCode);
  }
}
