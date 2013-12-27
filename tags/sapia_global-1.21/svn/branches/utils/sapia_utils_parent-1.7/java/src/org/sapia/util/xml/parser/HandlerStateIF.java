package org.sapia.util.xml.parser;


// Imports of David Meggison's SAX classes
// ---------------------------------------
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * The <CODE>HandlerStateIF</CODE> interface represents a givent "state" of an
 * XML parser that knows how to handle the events. The necessary logic to parse
 * a complex XML document can be split into multiple <CODE>HandlerStateIF</CODE>
 * where each instance has the logic for a specific XML element.<P>
 *
 * The <CODE>HandlerStateIF</CODE> is a modified SAX event handler. It provides
 * the main callback methods and each of have a extra parameter from the SAX
 * version: a <CODE>HandlerContextIF</CODE>. The context object is the controller
 * that contains the stack of current handler states and it is the bridge between
 * a <CODE>HandlerStateIF</CODE> and the SAX event handler.
 *
 * @see HandlerContextIF
 * @see StatefullSAXHandler
 * @author Jean-Cedric Desrochers
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface HandlerStateIF {
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
    throws SAXException;

  /**
   * Receives the notification of the the end of an element.
   *
   * @param aContext The handler context.
   * @param aLocalName The element type local name.
   * @param aQualifiedName The element type qualified name.
   * @exception SAXException If an exception occurs.
   */
  public void endElement(HandlerContextIF aContext, String anUri,
    String aLocalName, String aQualifiedName) throws SAXException;

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
    int anOffset, int length) throws SAXException;

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
    int anOffset, int aLength) throws SAXException;
}
