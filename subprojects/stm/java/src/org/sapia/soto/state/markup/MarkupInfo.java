package org.sapia.soto.state.markup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class holds the data necessary to output a given markup element (and its
 * attributes).
 *  
 * @author yduchesne
 * 
 * @see org.sapia.soto.state.markup.MarkupSerializer
 *
 */
public class MarkupInfo {

  private String name, prefix, uri;
  private List attributes = new ArrayList();
  
  public static class Attribute{
    String name, value;
    boolean encodeAsAttribute = false;
    
    Attribute(String name, String value){
      this.name = name;
      this.value = value;
    }
    
    public void encodeAsAttribute(){
      encodeAsAttribute = true;
    }
    
    public boolean isEncodeAsAttribute(){
      return encodeAsAttribute;
    }

    public String getName() {
      return name;
    }

    public String getValue() {
      return value;
    }
    
  }
  
  public List getAttributes(){
    if(attributes == null){
      return Collections.EMPTY_LIST;
    }
    return attributes;
  }

  public Attribute addAttribute(String name, String value){
    Attribute attr;
    if(attributes == null) attributes = new ArrayList(3);
    attributes.add(attr = new Attribute(name, value));
    return attr;
  }  

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }
  
  public boolean isNamespaceEnabled(){
    return uri != null && uri.length() > 0 && prefix != null && prefix.length() > 0;
  }

}
