package org.sapia.cocoon.generation.chunk.template.node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.sapia.cocoon.generation.chunk.template.Attribute;
import org.sapia.cocoon.generation.chunk.template.TemplateContext;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class ElementNode extends AbstractComplexNode{

  private List<Attribute> attributes = new ArrayList<Attribute>();
  private String localName, name, uri;
  
  public ElementNode(String localName, String name, String uri) {
    this.localName = localName;
    this. name = name;
    this.uri = uri;
  }
  
  public void addAttribute(Attribute attr){
    this.attributes.add(attr);
  }
  
  public void render(TemplateContext ctx) throws SAXException, IOException{
    ContentHandler handler = ctx.getContentHandler();
    handler.startElement(this.uri, this.localName, this.name, this.getAttributes(ctx));
    doRenderChildren(ctx);
    handler.endElement(uri, localName, name);
  }
  
  private Attributes getAttributes(TemplateContext ctx){
    AttributesImpl attrs = new AttributesImpl();
    for(Attribute attr:this.attributes){
      attrs.addAttribute(attr.getUri(), attr.getLocalName(), attr.getQName(), attr.getType(), attr.getValue().render(ctx));
    }
    return attrs;
  }
  

}
