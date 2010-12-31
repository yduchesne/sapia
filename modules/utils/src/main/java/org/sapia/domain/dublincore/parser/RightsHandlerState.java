package org.sapia.domain.dublincore.parser;


// Import of Sapia's domain classes
// --------------------------------
import org.sapia.domain.dublincore.Rights;

// Import of Sapia's utility classes
// ---------------------------------
import org.sapia.util.xml.parser.HandlerContextIF;
import org.sapia.util.xml.parser.ParserUtil;

// Imports of David Meggison's SAX classes
// ---------------------------------------
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */
public class RightsHandlerState extends AbstractDublinCoreHandlerState {
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////  INSTANCE ATTRIBUTES  /////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** Indicates if this handler state is currently parsing. */
  private boolean _isParsing;

  /** Buffer that contains the characters of the element beign parsed. */
  private StringBuffer _theElementContent;

  /** The result object of this handler state. */
  private Rights _theRights;

  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Creates a new RightsHandlerState instance.
   */
  public RightsHandlerState() {
    _isParsing           = false;
    _theElementContent   = new StringBuffer();
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  ACCESSOR METHODS  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns the result Rights object of this handler state.
   *
   * @return The result Rights object of this handler state.
   * @exception IllegalStateException If this handler state is currently parsing.
   */
  public Rights getResult() {
    if (_isParsing == true) {
      throw new IllegalStateException(
        "This RightsHandlerState is currently parsing");
    }

    return _theRights;
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////  INTERACE IMPLEMENTATION  ///////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Receives the notification of the the start of an element.
   *
   * @param aContext The handler context.
   * @param anUri The namespace URI associated with the element
   * @param aLocalName The element type local name.
   * @param aQualifiedName The element type qualified name.
   * @param someAttributes The specified or defaulted attributes.
   * @exception SAXException If an exception occurs.
   */
  public void startElement(HandlerContextIF aContext, String anUri,
    String aLocalName, String aQualifiedName, Attributes someAttributes)
    throws SAXException {
    // Validating the namespace URI of the call
    validateNamespace(anUri, aLocalName, aQualifiedName);

    if (ELEMENT_RIGHTS.equals(aLocalName)) {
      _isParsing = true;

      String anXmlLanguageCode = ParserUtil.extractXmlLanguageCode(someAttributes);
      _theRights = new Rights("", anXmlLanguageCode);
    }
  }

  /**
   * Receives the notification of the the end of an element.
   *
   * @param aContext The handler context.
   * @param anUri The namespace URI associated with the element
   * @param aLocalName The element type local name.
   * @param aQualifiedName The element type qualified name.
   * @exception SAXException If an exception occurs.
   */
  public void endElement(HandlerContextIF aContext, String anUri,
    String aLocalName, String aQualifiedName) throws SAXException {
    // Validating the namespace URI of the call
    validateNamespace(anUri, aLocalName, aQualifiedName);

    if (ELEMENT_RIGHTS.equals(aLocalName)) {
      _theRights.setValue(_theElementContent.toString());
      _isParsing = false;
    }
  }

  /**
   * Receives the notification of character data inside an element.
   *
   * @param aContext The handler context.
   * @param someChars The characters.
   * @param anOffset The start position in the character array.
   * @param aLength The number of characters to use from the character array.
   * @exception SAXException If an exception occurs.
   */
  public void characters(HandlerContextIF aContext, char[] someChars,
    int anOffset, int length) throws SAXException {
    _theElementContent.append(someChars, anOffset, length);
  }

  /**
   * Receives the notification of ignorable whitespace in element content.
   *
   * @param aContext The handler context.
   * @param someChars The whitespace characters.
   * @param anOffset The start position in the character array.
   * @param aLength The number of characters to use from the character array.
   * @exception SAXException If an exception occurs.
   */
  public void ignorableWhitespace(HandlerContextIF aContext, char[] someChars,
    int anOffset, int aLength) throws SAXException {
    // IGNORING WHITESPACES...
  }
}
