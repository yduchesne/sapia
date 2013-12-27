package org.sapia.domain.dublincore;


/**
 *
 *
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */
public class Source extends AbstractDublinCoreObject {
  /**
   * Creates a new Source instance with the value passed in.
   *
   * @param aValue The value of this source.
   */
  public Source(String aValue) {
    super(aValue);
  }

  /**
   * Creates a new Source instance with the parameters passed in.
   *
   * @param aValue The value of this source.
   * @param aLanguageCode The language code of this source.
   */
  public Source(String aValue, String aLanguageCode) {
    super(aValue, aLanguageCode);
  }
}
