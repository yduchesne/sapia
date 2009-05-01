package org.sapia.cocoon.generation.chunk.template;

import org.sapia.cocoon.generation.chunk.template.content.TemplateContent;

/**
 * Models an XML attribute. The value of the attribute supports embedded variables
 * (of the form: <code>$[inputModuleName:key]</code>).
 * 
 * @author yduchesne
 *
 */
public class Attribute {
  
  private String localName, qName, uri, type;
  private TemplateContent value;

  public Attribute(String localName, String qName, String uri, String type, TemplateContent value){
    this.localName = localName;
    this.qName = qName;
    this.uri = uri;
    this.type = type;
    this.value = value;
  }
  
  public String getLocalName() {
    return localName;
  }
  
  public String getQName() {
    return qName;
  }
  
  public String getUri() {
    return uri;
  }
  
  public String getType() {
    return type;
  }
 
  public TemplateContent getValue() {
    return value;
  }
}


