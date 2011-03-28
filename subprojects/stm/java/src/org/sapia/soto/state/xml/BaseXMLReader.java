/*
 * BaseXMLReader.java
 *
 * Created on April 6, 2005, 11:16 AM
 */

package org.sapia.soto.state.xml;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.XMLReader;

/**
 *
 * @author yduchesne
 */
public class BaseXMLReader implements XMLReader {
  
  private ContentHandler _handler;
  private DTDHandler _dtd;
  private EntityResolver _entity;
  private ErrorHandler _error;
  
  public BaseXMLReader() {
  }

  public boolean getFeature(String name) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException {
    return false;
  }

  public Object getProperty(String name) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException {
    return null;
  }

  /**
   * Throws <code>UnsupportedOperationException</code>. Should be overridden
   * if required.
   */ 
  public void parse(String systemId) throws java.io.IOException, org.xml.sax.SAXException {
    throw new UnsupportedOperationException();
  }

  /**
   * Throws <code>UnsupportedOperationException</code>. Should be overridden
   * if required.
   */   
  public void parse(org.xml.sax.InputSource input) throws java.io.IOException, org.xml.sax.SAXException {
    throw new UnsupportedOperationException();    
  }
  
  public void setContentHandler(ContentHandler handler) {
    _handler = handler;
  }  

  public void setFeature(String name, boolean value) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException {
  }

  public void setDTDHandler(org.xml.sax.DTDHandler handler) {
    _dtd = handler;
  }

  public void setProperty(String name, Object value) throws org.xml.sax.SAXNotRecognizedException, org.xml.sax.SAXNotSupportedException {
  }

  public void setErrorHandler(org.xml.sax.ErrorHandler handler) {
    _error = handler;
  }

  public void setEntityResolver(org.xml.sax.EntityResolver resolver) {
    _entity = resolver;
  }

  public org.xml.sax.ContentHandler getContentHandler() {
    return _handler;
  }

  public org.xml.sax.DTDHandler getDTDHandler() {
    return _dtd;
  }

  public org.xml.sax.EntityResolver getEntityResolver() {
    return _entity;
  }

  public org.xml.sax.ErrorHandler getErrorHandler() {
    return _error;
  }

  
}
