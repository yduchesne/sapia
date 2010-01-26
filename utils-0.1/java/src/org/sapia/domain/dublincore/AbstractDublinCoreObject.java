package org.sapia.domain.dublincore;


// Import of Sun's JDK classes
// ---------------------------
import java.io.Serializable;


/**
 *
 *
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */
public abstract class AbstractDublinCoreObject implements Serializable {
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////  INSTANCE ATTRIBUTES  /////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** The value of this abstract dublin core object. */
  private String _theValue;

  /** The language code of this abstract dublin core object. */
  private String _theLanguageCode;

  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Creates a new AbstractDublinCoreObject instance with the value passed in.
   *
   * @param aValue The value of this Dublin Core object.
   */
  protected AbstractDublinCoreObject(String aValue) {
    _theValue = aValue;
  }

  /**
   * Creates a new AbstractDublinCoreObject instance with the parameters passed in.
   *
   * @param aValue The value of this Dublin Core object.
   * @param aLanguageCode The language code of this Dublin Core object.
   */
  protected AbstractDublinCoreObject(String aValue, String aLanguageCode) {
    _theValue          = aValue;
    _theLanguageCode   = aLanguageCode;
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  ACCESSOR METHODS  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns the value of this Dublin Core object.
   *
   * @return The value of this Dublin Core object.
   */
  public String getValue() {
    return _theValue;
  }

  /**
   * Returns the language code of this Dublin Core object.
   *
   * @return The language code of this Dublin Core object.
   */
  public String getLanguageCode() {
    return _theLanguageCode;
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////  MUTATOR METHODS  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Changes the value of this Dublin Core object to the one passed in.
   *
   * @param The new value of this Dublin Core object.
   */
  public void setValue(String aValue) {
    _theValue = aValue;
  }

  /**
   * Changes the language code of this Dublin Core object to the value passed in.
   *
   * @param The new language of this Dublin Core object.
   */
  public void setLanguageCode(String aLanguageCode) {
    _theLanguageCode = aLanguageCode;
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////  HELPER METHODS  ////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns a string representation of this Dublin Core object.
   *
   * @return A string representation of this Dublin Core object.
   */
  public String toString() {
    StringBuffer aBuffer = new StringBuffer(super.toString());
    aBuffer.append("[value='").append(_theValue).append("'")
           .append(" languageCode='").append(_theLanguageCode).append("'")
           .append("]");

    return aBuffer.toString();
  }
}
