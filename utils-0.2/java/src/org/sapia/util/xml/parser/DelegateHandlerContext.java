package org.sapia.util.xml.parser;


// Imports of Meggison's SAX classes
// ---------------------------------
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * The <CODE>DelegateHandlerContext</CODE> class is an implementation of the
 * <CODE>HandlerStateIF</CODE> interface that act as a proxy and delegates the
 * <CODE>startElement()</CODE> method call to the underlying delegate object. This
 * delegate implementation is usefull to contain the handler of the root element of
 * an XML document.
 *
 * @see HandlerStateIF
 * @see StatefullSAXHandler
 * @author Jean-Cedric Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class DelegateHandlerContext implements HandlerStateIF {
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////  INSTANCE ATTRIBUTES  /////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  private HandlerStateIF _theDelegate;

  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   *
   */
  public DelegateHandlerContext(HandlerStateIF aDelegate) {
    _theDelegate = aDelegate;
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
    // Delegating the parsing of the element to the handler
    aContext.setCurrentState(_theDelegate, anUri, aLocalName, aQualifiedName,
      someAttributes);
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
    int anOffset, int aLength) throws SAXException {
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
  }
}
