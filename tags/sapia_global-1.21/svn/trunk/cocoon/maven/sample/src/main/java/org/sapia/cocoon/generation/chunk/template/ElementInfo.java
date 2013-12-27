package org.sapia.cocoon.generation.chunk.template;

import org.xml.sax.Attributes;

/**
 * Encapsulates data about an XML element.
 * 
 * @author yduchesne
 *
 */
public class ElementInfo {

  private String localName, name, uri;
  private Attributes attributes;
  
  public ElementInfo(String localName, String name, String uri, Attributes attrs) {
    this.localName = localName;
    this.name = name;
    this.uri = uri;
    this.attributes = attrs;
  }
  
  public String getLocalName() {
    return localName;
  }
  
  public String getName() {
    return name;
  }
  
  public String getUri() {
    return uri;
  }
  
  public Attributes getAttributes() {
    return attributes;
  }
}
