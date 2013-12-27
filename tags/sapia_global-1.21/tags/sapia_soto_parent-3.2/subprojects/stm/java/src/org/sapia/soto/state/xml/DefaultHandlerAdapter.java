/*
 * DefaultHandlerAdapter.java
 *
 * Created on April 11, 2005, 9:15 AM
 */

package org.sapia.soto.state.xml;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author yduchesne
 */
public class DefaultHandlerAdapter extends DefaultHandler{
  
  private ContentHandler _handler;
  
  /** Creates a new instance of DefaultHandlerAdapter */
  public DefaultHandlerAdapter(ContentHandler handler) {
    _handler = handler;
  }
  public void characters(char[] ch, int start, int length) throws SAXException{
    _handler.characters(ch, start, length);
  }
  public void endDocument() throws SAXException{
    _handler.endDocument();
  }
  public void endElement(String uri, String localName, String qName) throws SAXException{
    _handler.endElement(uri, localName, qName);
  }
  public void endPrefixMapping(String prefix) throws SAXException{
    _handler.endPrefixMapping(prefix);
  }
  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException{
    _handler.ignorableWhitespace(ch, start, length);
  }
  public void processingInstruction(String target, String data) throws SAXException{
    _handler.processingInstruction(target, data);
  }
  public void setDocumentLocator(Locator locator){
    _handler.setDocumentLocator(locator);
  }
  public void skippedEntity(String name) throws SAXException{
    _handler.skippedEntity(name);
  }
  public void startDocument() throws SAXException{
    _handler.startDocument();
  }
  public void startElement(String uri, 
                           String localName, 
                           String qName, 
                           Attributes atts) throws SAXException{
    _handler.startElement(uri, localName,  qName, atts);
  }
  public void startPrefixMapping(String prefix, 
                                 String uri) throws SAXException{
    _handler.startPrefixMapping(prefix, uri);
  }
  
}
