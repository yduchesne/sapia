package org.sapia.util.xml.parser;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * The <CODE>StatefullSAXHandler</CODE> class is a SAX event handler. When one of is callback
 * methods is called, it delegates the call to the current handler object; that current
 * handler is refered as the "current state" of the handler. It accesses the <CODE>HandlerStateIF</CODE>
 * instance by using a <CODE>HandlerContextIF</CODE>. That context is passed at creation
 * and contains the stack of all the handlers as well as their result objects.
 *
 * @see HandlerContextIF
 * @see HandlerStateIF
 * @author Jean-Cedric Desrochers
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class StatefullSAXHandler extends DefaultHandler {

  /** Defines the logger instance for this class. */
  //private static Logger _theLogger = Logger.getLogger(StatefullSAXHandler.class);

  /** The context of this handler. */
  private HandlerContextIF _theContext;

  /** The locator of this parser. */
  private Locator _theLocator;
  
  /** The SAX exception that occured in the eventuality of an error. */
  private SAXException _saxException;

  /**
   * Creates a new StatefullSAXHandler instance with the argument passed in.
   *
   * @param aContext The handler context.
   */
  public StatefullSAXHandler(HandlerContextIF aHandlerContext) {
    _theContext = aHandlerContext;
  }

  /**
   * Returns a textual description of the current document position with the following
   * format: "<CODE>Document position: line number=x  column number=y</CODE>".
   *
   * @return A textual description of the current document position.
   */
  public String getDocumentPosition() {
    StringBuffer aBuffer = new StringBuffer();
    aBuffer.append("Document position: line number=")
           .append(_theLocator.getLineNumber()).append("  column number=")
           .append(_theLocator.getColumnNumber());

    return aBuffer.toString();
  }
  
  /**
   * Returns the SAX exception that could occur in the processing.
   * 
   * @return The SAXException instance or <code>null</code> if no error occured.
   */
  public SAXException getSaxException() {
    return _saxException;
  }

  /**
   * Overrides the default implementation of the ContentHandler interface and allow the
   * application to resolve external entities.
   *
   * @param aPublicID The public identifier of the external entity being referenced, or null if none was supplied.
   * @param aSystemID The system identifier of the external entity being referenced.
   */
  public InputSource resolveEntity(String aPublicID, String aSystemID) throws SAXException {
    // Logging a debug message
//    if (_theLogger.isDebugEnabled()) {
//      StringBuffer aBuffer = new StringBuffer("Method resolveEntity() is beign called with");
//      aBuffer.append(" publicID=").append(aPublicID).
//              append(" systemID=").append(aSystemID);
//      _theLogger.debug(aBuffer.toString());
//    }
    return null;
  }

  /**
   * Overrides the default implementation of the ContentHandler interface and receive
   * notification of a notation declaration event.
   *
   * @param aName The notation name.
   * @param aPublicId The notation's public identifier, or null if none was given.
   * @param aSystemId The notation's system identifier, or null if none was given.
   */
  public void notationDecl(String aName, String aPublicID, String aSystemID) throws SAXException {
    // Logging a debug message
//    if (_theLogger.isDebugEnabled()) {
//      StringBuffer aBuffer = new StringBuffer("Method notationDecl() is beign called with");
//      aBuffer.append(" name=").append(aName).
//              append(" publicID=").append(aPublicID).
//              append(" systemID=").append(aSystemID);
//      _theLogger.debug(aBuffer.toString());
//    }
  }

  /**
   * Overrides the default implementation of the ContentHandler interface and receive
   * notification of an unparsed entity declaration event.
   *
   * @param aName The unparsed entity name.
   * @param aPublicId The notation's public identifier, or null if none was given.
   * @param aSystemId The notation's system identifier, or null if none was given.
   * @param aNotationName The name of the associated notation.
   */
  public void unparsedEntityDecl(String aName, String aPublicID, String aSystemID, String aNotationName) throws SAXException {
    // Logging a debug message
//    if (_theLogger.isDebugEnabled()) {
//      StringBuffer aBuffer = new StringBuffer("Method unparsedEntityDecl() is beign called with");
//      aBuffer.append(" name=").append(aName).
//              append(" publicID=").append(aPublicID).
//              append(" systemID=").append(aSystemID).
//              append(" notationName=").append(aNotationName);
//      _theLogger.debug(aBuffer.toString());
//    }
  }

  /**
   * Overrides the default implementation of the ContentHandler interface and receives
   * the Locator object for document positions.
   *
   * @param aLocator A locator for all SAX document events.
   * @see org.xml.sax.ContentHandler#setDocumentLocator
   */
  public void setDocumentLocator(Locator aLocator) {
    _theLocator = aLocator;
  }

  /**
   * Overrides the default implementation of the ContentHandler interface and forwards the
   * notification of the beginning of the document to the current HandleStateIF object of
   * this parser.
   *
   * @exception SAXException Any SAX exception, possibly wrapping another exception.
   * @see org.xml.sax.ContentHandler#startDocument
   */
  public void startDocument() throws SAXException {
    // Logging a debug message
//    if (_theLogger.isDebugEnabled()) {
//      _theLogger.debug("Method startDocument() is beign called");
//    }
  }

  /**
   * Overrides the default implementation of the ContentHandler interface and forwards the
   * notification of the end of the document to the current HandleStateIF object of
   * this parser.
   *
   * @exception SAXException Any SAX exception, possibly wrapping another exception.
   * @see org.xml.sax.ContentHandler#endDocument
   */
  public void endDocument() throws SAXException {
    // Logging a debug message
//    if (_theLogger.isDebugEnabled()) {
//      _theLogger.debug("Method endDocument() is beign called");
//    }
  }

  /**
   * Overrides the default implementation of the ContentHandler interface and sets the
   * namespace URI passed in as the current mapping for the given prefix.
   *
   * @param aPrefix The Namespace prefix being declared.
   * @param anUri The Namespace URI mapped to the prefix.
   * @exception SAXException Any SAX exception, possibly wrapping another exception.
   */
  public void startPrefixMapping(String aPrefix, String anUri) throws SAXException {
    // Logging an info message
//    if (_theLogger.isDebugEnabled()) {
//      _theLogger.debug("Starts mapping of the prefix [" + aPrefix +
//              "] to the namespace URI [" + anUri + "].");
//    }

    try {
      _theContext.startPrefixMapping(aPrefix, anUri);

    } catch (RuntimeException re) {
      final String aMessage = "RuntimeException caugh in method startPrefixMapping().";
      _saxException = new SAXException(aMessage, re);
      throw _saxException;
    }
  }

  /**
   * Overrides the default implementation of the ContentHandler interface and removes
   * the current mapping of the prefix passed in.
   *
   * @param aPrefix The Namespace prefix being declared.
   * @exception SAXException Any SAX exception, possibly wrapping another exception.
   * @see org.xml.sax.ContentHandler#endPrefixMapping
   */
  public void endPrefixMapping(String aPrefix) throws SAXException {
    // Logging an info message
//    if (_theLogger.isDebugEnabled()) {
//      _theLogger.debug("Ends mapping of the prefix [" + aPrefix + "].");
//    }

    try {
      _theContext.endPrefixMapping(aPrefix);
    } catch (RuntimeException re) {
      final String aMessage = "RuntimeException caugh in method endPrefixMapping().";
      _saxException = new SAXException(aMessage, re);
      throw _saxException;
    }
  }

  /**
   * Overrides the default implementation of the ContentHandler interface and forwards
   * the notification of the the start of an element to the current HandleStateIF
   * object of this parser.
   *
   * @param anUri The namespace URI associated with the element
   * @param aLocalName The element type local name.
   * @param aQualifiedName The element type qualified name.
   * @param someAttributes The specified or defaulted attributes.
   * @exception SAXException Any SAX exception, possibly wrapping another exception.
   * @see org.xml.sax.ContentHandler#startElement
   */
  public void startElement(String anUri, String aLocalName,
    String aQualifiedName, Attributes someAttributes) throws SAXException {
    try {
      _theContext.getCurrentState().startElement(_theContext, anUri,
        aLocalName, aQualifiedName, someAttributes);
      
    } catch (SAXException se) {
      _saxException = se;
      throw se;
      
    } catch (RuntimeException re) {
      final String aMessage = "RuntimeException caugh in method startElement().";
      _saxException = new SAXException(aMessage, re);
      throw _saxException;
    }
  }

  /**
   * Overrides the default implementation of the ContentHandler interface and forwards
   * the notification of the the end of an element to the current HandleStateIF
   * object of this parser.
   *
   * @param anUri The namespace URI associated with the element
   * @param aLocalName The element type local name.
   * @param aQualifiedName The element type qualified name.
   * @param someAttributes The specified or defaulted attributes.
   * @exception SAXException Any SAX exception, possibly wrapping another exception.
   * @see org.xml.sax.ContentHandler#endElement
   */
  public void endElement(String anUri,
          String aLocalName, String aQualifiedName) throws SAXException {
    try {
      _theContext.getCurrentState().endElement(_theContext, anUri, aLocalName,
        aQualifiedName);
      
    } catch (SAXException se) {
      _saxException = se;
      throw se;
      
    } catch (RuntimeException re) {
      final String aMessage = "RuntimeException caugh in method endElement().";
      _saxException = new SAXException(aMessage, re);
      throw _saxException;
    }
  }

  /**
   * Overrides the default implementation of the ContentHandler interface and forwards
   * the notification of character data inside an element to the current HandleStateIF
   * object of this parser.
   *
   * @param someChars The characters.
   * @param anOffset The start position in the character array.
   * @param aLength The number of characters to use from the character array.
   * @exception SAXException Any SAX exception, possibly wrapping another exception.
   * @see org.xml.sax.ContentHandler#characters
   */
  public void characters(char[] someChars, int anOffset, int aLength) throws SAXException {
    try {
      _theContext.getCurrentState().characters(_theContext, someChars,
        anOffset, aLength);
      
    } catch (SAXException se) {
      _saxException = se;
      throw se;
      
    } catch (RuntimeException re) {
      final String aMessage = "RuntimeException caugh in method character().";
      _saxException = new SAXException(aMessage, re);
      throw _saxException;
    }
  }

  /**
   * Overrides the default implementation of the ContentHandler interface and forwards
   * the notification of ignorable whitespace in element content to the current
   * HandleStateIF object of this parser.
   *
   * @param someChars The whitespace characters.
   * @param anOffset The start position in the character array.
   * @param aLength The number of characters to use from the character array.
   * @exception SAXException Any SAX exception, possibly wrapping another exception.
   * @see org.xml.sax.ContentHandler#ignorableWhitespace
   */
  public void ignorableWhitespace(char[] someChars, int anOffset, int aLength) throws SAXException {
    try {
      _theContext.getCurrentState().ignorableWhitespace(_theContext, someChars,
        anOffset, aLength);
      
    } catch (SAXException se) {
      _saxException = se;
      throw se;
      
    } catch (RuntimeException re) {
      final String aMessage = "RuntimeException caugh in method ignorableWhitespace().";
      _saxException = new SAXException(aMessage, re);
      throw _saxException;
    }
  }

  /**
   * Overrides the default implementation of the ErrorHandler interface logs a warning
   * message of the warning and the current document position of the parser.
   *
   * @param aWarning The warning information encoded as an exception.
   * @exception SAXException Any SAX exception, possibly wrapping another exception.
   * @see org.xml.sax.ErrorHandler#warning
   */
  public void warning(SAXParseException aWarning) throws SAXException {
    _saxException = aWarning;
  }

  /**
   * Overrides the default implementation of the ErrorHandler interface logs a critical
   * message of the error and the current document position of the parser.
   *
   * @param anError The error information encoded as an exception.
   * @exception SAXException Any SAX exception, possibly wrapping another exception.
   * @see org.xml.sax.ErrorHandler#warning
   * @see org.xml.sax.SAXParseException
   */
  public void error(SAXParseException anError) throws SAXException {
    _saxException = anError;
  }

  /**
   * Overrides the default implementation of the ErrorHandler interface logs an error
   * message of the fatal error and the current document position of the parser.
   *
   * @param aFatalError The fatal error information encoded as an exception.
   * @exception SAXException Any SAX exception, possibly wrapping another exception.
   * @see org.xml.sax.ErrorHandler#fatalError
   * @see org.xml.sax.SAXParseException
   */
  public void fatalError(SAXParseException aFatalError) throws SAXException {
    _saxException = aFatalError;
    throw aFatalError;
  }
}
