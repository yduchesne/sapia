package org.sapia.util.xml.confix;


// Import of Sapia's utility classes
// ---------------------------------
import org.sapia.util.xml.XmlUtil;
import org.sapia.util.xml.parser.HandlerContextIF;
import org.sapia.util.xml.parser.HandlerStateIF;

// Imports of David Meggison's SAX classes
// ---------------------------------------
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * Implements a <code>HandlerStateIF</code> that is used by the <code>SAXProcessor</code>.
 *
 * @author JC Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ConfixHandlerState implements HandlerStateIF {
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////  INSTANCE ATTRIBUTES  /////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** Indicates if this handler state is currently parsing. */
  private boolean _isParsing;

  /** Indicates if this handler state is currently parsing a child element. */
  private boolean _isParsingChild;

  /** The SAX processor that manage this handler state. */
  private SAXProcessor _theProcessor;

  /** Buffer that contains the characters of the element beign parsed. */
  private StringBuffer _theContent;

  /** The parent object that represent the parent element beign parsed. */
  private Object _theParentObject;

  /** The current object that represent the element beign parsed. */
  private Object _theCurrentObject;

  /** Indicates if the current object has been assigned to its parent. */
  private boolean _isCurrentObjectAssigned;

  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Creates a new ConfixHandlerState instance
   */
  public ConfixHandlerState(SAXProcessor aProcessor, Object aParentObject) {
    if (aParentObject == null) {
      throw new IllegalArgumentException("The parent object passed in is null");
    }

    _theContent        = new StringBuffer();
    _theProcessor      = aProcessor;
    _theParentObject   = aParentObject;
  }

  /**
   * Creates a new ConfixHandlerState instance
   */
  public ConfixHandlerState(SAXProcessor aProcessor) {
    _theContent     = new StringBuffer();
    _theProcessor   = aProcessor;
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  ACCESSOR METHODS  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns the result object of this handler state.
   *
   * @return The result object of this handler state.
   * @exception IllegalStateException If this handler state is currently parsing.
   */
  public Object getResult() {
    if (_isParsing == true) {
      throw new IllegalStateException("This handler state is currently parsing");
    }

    return _theCurrentObject;
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
    if (!_isParsing) {
      _isParsing = true;

      CreationStatus aStatus = null;
      String         prefix = XmlUtil.extractPrefix(aQualifiedName);

      try {
        // Creating the object
        aStatus   = _theProcessor.getObjectFactory().newObjectFor(prefix,
            anUri, aLocalName, _theParentObject);

        _theCurrentObject          = aStatus.getCreated();
        _isCurrentObjectAssigned   = aStatus.wasAssigned();

        // Process the attributes of the element
        for (int i = 0; i < someAttributes.getLength(); i++) {
          String anAttributeName  = someAttributes.getLocalName(i);
          String anAttributeValue = someAttributes.getValue(i);

          AbstractXMLProcessor.invokeSetter(aLocalName, _theCurrentObject,
            anAttributeName, anAttributeValue);
        }
      } catch (ObjectCreationException oce) {
        if (_theParentObject == null) {
          String aMessage = "Unable to create an object for the element " +
            aQualifiedName;

          throw new SAXException(aMessage, oce);
        }
      } catch (ConfigurationException ce) {
        String aMessage = "Unable to process the content of the element " +
          aLocalName;

        throw new SAXException(aMessage, ce);
      }
    } else {
      HandlerStateIF aChildHandler;

      if (_theCurrentObject instanceof HandlerStateIF) {
        aChildHandler = (HandlerStateIF) _theCurrentObject;
      } else {
        aChildHandler = new ConfixHandlerState(_theProcessor, _theCurrentObject);
      }

      _isParsingChild = true;
      aContext.setCurrentState(aChildHandler, anUri, aLocalName,
        aQualifiedName, someAttributes);
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
    if (_isParsingChild) {
      _isParsingChild = false;
    } else {
      String aValue = _theContent.toString().trim();

      if (aValue.length() > 0) {
        if (_theCurrentObject != null) {
          try {
            AbstractXMLProcessor.invokeSetter(aLocalName, _theCurrentObject,
              "Text", aValue);
          } catch (ConfigurationException ce) {
            String aMessage = "The object " +
              _theCurrentObject.getClass().getName() +
              " does not accept free text";

            throw new SAXException(aMessage, ce);
          }
        } else {
          try {
            AbstractXMLProcessor.invokeSetter(_theParentObject.getClass()
                                                              .getName(),
              _theParentObject, aLocalName, aValue);
            _isCurrentObjectAssigned = true;
          } catch (ConfigurationException ce) {
            String aMessage = "Unable to process the content of the element " +
              aLocalName;

            throw new SAXException(aMessage, ce);
          }
        }
      } else if (_theCurrentObject == null) {
        String aMessage = "Unable to create an object for the element " +
          aQualifiedName;

        throw new SAXException(aMessage);
      }

      if (_theCurrentObject instanceof ObjectCreationCallback) {
        try {
          _theCurrentObject = ((ObjectCreationCallback) _theCurrentObject).onCreate();
        } catch (ConfigurationException e) {
          throw new SAXException("Could not create object", e);
        }
      }

      if (_theCurrentObject instanceof NullObject) {
        _theCurrentObject = null;
      }
      else  if (_theParentObject instanceof NullObject) {
        _theParentObject = _theCurrentObject;
      }
      // assign obj to parent through setXXX or addXXX
      else  if ((_theParentObject != null) && !_isCurrentObjectAssigned &&
            !(_theCurrentObject instanceof NullObject)) {
        try {
          AbstractXMLProcessor.assignToParent(_theParentObject,
            _theCurrentObject, aLocalName);
        } catch (ConfigurationException ce) {
          StringBuffer aBuffer = new StringBuffer(
              "Unable to assign the object ").append(_theCurrentObject)
                                             .append(" to the parent ")
                                             .append(_theParentObject.getClass()
                                                                                                         .getName());

          throw new SAXException(aBuffer.toString(), ce);
        }
      }

      _isParsing = false;
      aContext.removeCurrentState(anUri, aLocalName, aQualifiedName);
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
    _theContent.append(someChars, anOffset, length);
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
