package org.sapia.cocoon.util;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class StdoutContentHandler implements ContentHandler{

  int level = 0;
  public void startDocument() throws SAXException {
    
  }
  
  public void endDocument() throws SAXException {
    
  }
  
  public void characters(char[] ch, int start, int length) throws SAXException {
    System.out.println(indent()+new String(ch,start,length).trim());
  }
  
  public void startElement(String uri, String localName, String name,
      Attributes atts) throws SAXException {
    System.out.println(indent()+"<"+localName+toString(atts) + ">");
    level++;
  }
  
  public void endElement(String uri, String localName, String name)
      throws SAXException {
    level--;
    System.out.println(indent()+"</"+localName+">");
  }
  
  public void ignorableWhitespace(char[] ch, int start, int length)
      throws SAXException {
    //System.out.print(new String(ch, start, length));
  }
  public void skippedEntity(String name) throws SAXException {
  }
  public void setDocumentLocator(Locator locator) { 
  }
  public void processingInstruction(String target, String data)
      throws SAXException {
  }
  public void startPrefixMapping(String prefix, String uri) throws SAXException {
  }
  public void endPrefixMapping(String prefix) throws SAXException {
  }
  
  private String toString(Attributes atts){
    StringBuilder s = new StringBuilder();
    for(int i = 0; i < atts.getLength(); i++){
      if(i == 0) s.append(' ');
      s.append(atts.getLocalName(i)).append('=').append('"')
        .append(atts.getValue(i)).append('"');
      if(i < atts.getLength() - 1){
        s.append(' ');
      }
    }
    return s.toString();
  }
  
  private String indent(){
    StringBuilder s = new StringBuilder();
    for(int i = 0; i < level; i++){
      s.append("  ");
    }
    return s.toString();
  }

}
