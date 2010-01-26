package org.sapia.domain.dublincore.parser;


// Import of Sapia's utility classes
// ---------------------------------
import org.sapia.util.xml.parser.HandlerStateIF;

// Imports of David Meggison's SAX classes
// ---------------------------------------
import org.xml.sax.SAXException;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */
public abstract class AbstractDublinCoreHandlerState
  implements DublinCoreDictionaryIF, HandlerStateIF {
  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Creates a new AbstractDublinCoreHandlerState instance.
   */
  protected AbstractDublinCoreHandlerState() {
    // TO HIDE DIRECT CREATION
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////  HELPER METHODS  ////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Validates the namespace URI passed in.
   *
   * @exception SAXException If the namespace passed in is not the
   *            Dublin Core namespace identifier.
   */
  public void validateNamespace(String aNamespaceURI, String aLocalName,
    String aQualifiedName) throws SAXException {
    if (!DUBLIN_CORE_NAMESPACE_URI.equals(aNamespaceURI)) {
      String aMessage =
        "This handler is parsing an XML element of an unknown namespace: " +
        formatElement(aNamespaceURI, aLocalName) +
        ". This handler recognizes only the namespace " +
        DUBLIN_CORE_NAMESPACE_URI;
      throw new SAXException(aMessage);
    }
  }

  /**
   * This method returns a string representation of the element passed in.
   *
   * @return A string representation of the element passed in.
   */
  public String formatElement(String anUri, String aLocalName) {
    StringBuffer aBuffer = new StringBuffer();
    aBuffer.append(anUri).append(":").append(aLocalName);

    return aBuffer.toString();
  }
}
