package org.sapia.cocoon.generation.chunk.template.node;

import org.sapia.cocoon.generation.chunk.template.Node;
import org.sapia.cocoon.generation.chunk.template.TemplateContext;
import org.xml.sax.SAXException;

public class SkippedEntityNode implements Node{
  
  private String name;
  
  public SkippedEntityNode(String name){
    this.name = name;
  }
  
  public void render(TemplateContext ctx) throws SAXException {
    ctx.getContentHandler().skippedEntity(name);
  }

}
