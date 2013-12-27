/**
 * 
 */
package org.sapia.soto.me.xml;

import java.io.IOException;
import java.io.InputStream;

import javolution.xml.stream.XMLStreamConstants;
import javolution.xml.stream.XMLStreamException;
import javolution.xml.stream.XMLStreamReaderImpl;

import org.sapia.soto.me.ConfigurationException;
import org.sapia.soto.me.J2meService;
import org.sapia.soto.me.util.Log;
import org.sapia.soto.me.util.PropertyResolver;
import org.sapia.soto.me.util.Template;
import org.sapia.soto.me.xml.XmlConsumer.XmlCursor;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class J2meConfixProcessor implements XmlCursor {

  private static final String MODULE_NAME = "ConfixProcessor";
  
  /** The object factory of this processor. */
  private ObjectFactory _factory;
  
  /** The property resolver of this processor */
  private PropertyResolver _propertyResolver;
  
  /** The xml reader of this processor. */
  private XMLStreamReaderImpl _xmlReader;
  
  /**
   * Creates a new J2meConfixProcessor instance.
   * 
   * @param aFactory The object factory to use for this processor.
   * @param aResolver The property resolver.
   */
  public J2meConfixProcessor(ObjectFactory aFactory, PropertyResolver aResolver) {
    _factory = aFactory;
    _propertyResolver = aResolver;
  }
  
  /**
   * This method takes an XML stream as input and returns an object representation of the passed-in XML.
   *
   * @param anInput The XML input stream to process.
   * @return An object representation of the XML stream.
   * @throws J2meProcessingException If an error occurs processing the input stream.
   */
  public Object process(InputStream anInput) throws J2meProcessingException {
    try {
      // Create javolution xml reader
      _xmlReader = new XMLStreamReaderImpl();
      _xmlReader.setInput(anInput, "UTF-8");
      
      // Process the xml stream
      skipProcessingInstructions();
      Object aResult = processElement(null);
      return aResult;
      
    } catch (XMLStreamException xse) {
      String message = "Error parsing the XML of the input stream";
      Log.error(MODULE_NAME, message, xse);
      throw new J2meProcessingException(message, xse);
      
    } finally {
      try {
        if (anInput != null) {
          anInput.close();
        }
      } catch (IOException ioe) {
        String aMessage = "Error closing the XML input stream processed by the processor";
        Log.warn(MODULE_NAME, aMessage, ioe);
        throw new J2meProcessingException(aMessage, ioe);
      }
    }
  }
  
  /**
   * Internal method that will process each element of the XML stream an applying the
   * logic of the J2meConfix runtime.
   * 
   * @param aParent The parent object on which the current element applies.
   * @return The result object of the processing.
   * @throws J2meProcessingException
   */
  protected Object processElement(Object aParent) throws J2meProcessingException {
    String anElementName = _xmlReader.getLocalName().toString();
    String aPrefix = null;
    if (_xmlReader.getPrefix() != null) {
      aPrefix = _xmlReader.getPrefix().toString();
    }
    String aNamespaceURI = null;
    if (_xmlReader.getNamespaceURI() != null) {
      aNamespaceURI = _xmlReader.getNamespaceURI().toString();
    } else {
      String message = "The namespace URI of the element '"+ anElementName + "' is not define in the XML document";
      Log.error(MODULE_NAME, message);
      throw new J2meProcessingException(message);
    }

    // Create object using registered object facory
    Object newObject = null;
    try {
      newObject = _factory.newObjectFor(aPrefix, aNamespaceURI, anElementName, aParent);
      
    } catch (ObjectCreationException oce) {
      if (aParent == null) {
        String message = "Unable to create an object for the element '" + anElementName + "'";
        Log.error(MODULE_NAME, message, oce);
        throw new J2meProcessingException(message, oce);
      }
    } 
    
    // If no object created, look if parent is an ObjectFactory
    if (newObject == null && aParent instanceof ObjectFactory) {
      try {
        newObject = ((ObjectFactory) aParent).newObjectFor(aPrefix, aNamespaceURI, anElementName, aParent);
      
      } catch (ObjectCreationException oce) {
        String message = "Unable to create an object for the element '" + anElementName + "'";
        Log.error(MODULE_NAME, message, oce);
        throw new J2meProcessingException(message, oce);
      }
    }
      
    try {
      
      // Process attributes of the current element
      for (int i = 0; i < _xmlReader.getAttributeCount() && newObject != null; i++) {
        String name = _xmlReader.getAttributeLocalName(i).toString();
        String value = _xmlReader.getAttributeValue(i).toString();
        assignValue(anElementName, newObject, name, interpolateValue(value));
      }

      // Process the content of the element
      Object childObject = null;
      boolean isElementConsumed = false;
      while (!isElementConsumed && _xmlReader.next() != XMLStreamConstants.END_ELEMENT) {

        // Process the child elements of the current element
        if (_xmlReader.getEventType() == XMLStreamConstants.START_ELEMENT) {
          if (newObject instanceof XmlConsumer) {
            ((XmlConsumer) newObject).consumeXml(this);
            isElementConsumed = true;
          } else {
            childObject = processElement(newObject);
            if (newObject == null) {
              newObject = childObject;
            }
          }

        // Process the content if no object created
        } else if (_xmlReader.getEventType() == XMLStreamConstants.CHARACTERS) {
          if (newObject == null && _xmlReader.getText().toString().trim().length() > 0) {
            assignValue(anElementName+".TEXT", aParent, anElementName, interpolateValue(_xmlReader.getText().toString().trim()));
            newObject = NullObject.getInstance();
          }
        }
      }
      
    } catch (XMLStreamException xse) {
      String message = "Error processing child elements of '" + anElementName + "'";
      Log.error(MODULE_NAME, message, xse);
      throw new J2meProcessingException(message, xse);
    }
    
    if (newObject == null) {
      String message = "Unable to create an object for the element '" + anElementName + "'";
      Log.error(MODULE_NAME, message);
      throw new J2meProcessingException(message);
    }
    
    // Finalize the processing of the current element
    try {
      // Check if object implements ObjectCreationCallback
      if (newObject instanceof ObjectCreationCallback) {
        newObject = ((ObjectCreationCallback) newObject).onCreate();
      }
    
      // assign new object to the parent
      if (!(newObject instanceof NullObject) && aParent != null) {
        assignValue(anElementName+".PARENT", aParent, anElementName, newObject);
      }
      
      // return value according to use case 
      if (newObject instanceof NullObject) {
        return null;
      } else {
        return newObject;
      }
      
    } catch (ConfigurationException ce) {
      throw new J2meProcessingException("Error processing the end of the element '" + anElementName + "'", ce);
    }
  }
  
  /**
   * Internal method that assigns a value (name-object pair) the object created for an XML element.
   *  
   * @param anElementName The XML element name for which the object was created.
   * @param anObject The object on which the assignment should be done.
   * @param aValueName The name of the value to assign.
   * @param aValue The value to assign
   * @throws J2meProcessingException If an error occurs.
   */
  protected void assignValue(String anElementName, Object anObject, String aValueName, Object aValue) throws J2meProcessingException {
    try {
      if (anObject instanceof ObjectHandler) {
        ((ObjectHandler) anObject).handleObject(aValueName, aValue);
      } else if (anObject instanceof J2meService) {
        ((J2meService) anObject).handleObject(aValueName, aValue);
      } else {
        String message = "Unable to assign the value '" + aValueName+ "=" + aValue +
                "' of the XML element '" + anElementName + "' on the object " + anObject +
                " - the object is neither an ObjectHandler nor a J2meService";
        Log.error(MODULE_NAME, message);
        throw new J2meProcessingException(message);
      }
      
    } catch (ConfigurationException ce) {
      String message = "Error assigning the value '" + aValueName+ "=" + aValue +
              "' of the XML element '" + anElementName + "' on the object " + anObject;
      Log.error(MODULE_NAME, message, ce);
      throw new J2meProcessingException(message, ce);
    }
  }
  
  /**
   * Interpolated the string value passed in for variable defined using format "${variable}".
   * 
   * @param aValue The string to interpolate.
   * @return The interpolated value.
   */
  protected String interpolateValue(String aValue) {
    Template template = Template.parse(aValue);
    return template.render(_propertyResolver);
  }
  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.XmlConsumer.XmlCursor#processXmlElement(java.lang.Object)
   */
  public Object processXmlElement(Object aParent) throws J2meProcessingException {
    Object newObject = processElement(aParent);
    goToEndOfCurrentElement();

    return newObject;
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.XmlConsumer.XmlCursor#skipXmlElement()
   */
  public void skipXmlElement() throws J2meProcessingException {
    try {
      logXmlReaderState("skipXmlElement().START");
      do {
        if (_xmlReader.getEventType() == XMLStreamConstants.START_ELEMENT) {
          _xmlReader.next();
          skipXmlElement();
          
        } else if (_xmlReader.getEventType() != XMLStreamConstants.END_ELEMENT) {
          _xmlReader.next();
        }

        logXmlReaderState("skipXmlElement().DO   ");
      } while (_xmlReader.getEventType() != XMLStreamConstants.END_ELEMENT);

      _xmlReader.next();
      logXmlReaderState("skipXmlElement().END  ");
      
    } catch (XMLStreamException xse) {
      String message = "Error skipping xml elements";
      Log.error(MODULE_NAME, message, xse);
      throw new J2meProcessingException(message, xse);
    }
  }
  
  protected void goToEndOfCurrentElement() throws J2meProcessingException {
    try {
      logXmlReaderState("goToEndOfCurrentElement().START");
      do {
        logXmlReaderState("goToEndOfCurrentElement()");
      } while (_xmlReader.next() != XMLStreamConstants.END_ELEMENT);
      logXmlReaderState("goToEndOfCurrentElement().END");
      
    } catch (XMLStreamException xse) {
      String message = "Error skipping xml elements";
      Log.error(MODULE_NAME, message, xse);
      throw new J2meProcessingException(message, xse);
    }
  }
  
  protected void skipProcessingInstructions() throws J2meProcessingException {
    try {
      logXmlReaderState("skipProcessingInstructions().START");
      do {
        logXmlReaderState("skipProcessingInstructions()");
      } while (_xmlReader.next() != XMLStreamConstants.START_ELEMENT);
      logXmlReaderState("skipProcessingInstructions().END");
      
    } catch (XMLStreamException xse) {
      String message = "Error skipping xml elements";
      Log.error(MODULE_NAME, message, xse);
      throw new J2meProcessingException(message, xse);
    }
  }
  
  protected void logXmlReaderState(String aSource) {
//    if (_xmlReader.getEventType() == XMLStreamConstants.START_ELEMENT) {
//      Log.debug(MODULE_NAME, aSource + ":  XmlReader::START_ELEMENT  <"+_xmlReader.getQName().toString()+">");
//    } else if (_xmlReader.getEventType() == XMLStreamConstants.CHARACTERS) {
//      Log.debug(MODULE_NAME, aSource + ":  XmlReader::CHARACTERS  ["+_xmlReader.getText().toString().trim());
//    } else if (_xmlReader.getEventType() == XMLStreamConstants.END_ELEMENT) {
//      Log.debug(MODULE_NAME, aSource + ":  XmlReader::END_ELEMENT  </"+_xmlReader.getQName().toString()+">");
//    } else {
//      Log.debug(MODULE_NAME, aSource + ":  XmlReader::{unknown}  eventType="+_xmlReader.getEventType());
//    }
  }
}
