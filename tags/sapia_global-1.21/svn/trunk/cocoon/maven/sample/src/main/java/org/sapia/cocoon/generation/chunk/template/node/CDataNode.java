package org.sapia.cocoon.generation.chunk.template.node;

import org.sapia.cocoon.generation.chunk.template.Node;
import org.sapia.cocoon.generation.chunk.template.TemplateContext;
import org.sapia.cocoon.generation.chunk.template.content.TemplateContent;
import org.xml.sax.SAXException;

public class CDataNode implements Node{
  
  private TemplateContent content;
  
  public CDataNode(TemplateContent content) {
    this.content = content;
  }
  
  public void render(TemplateContext ctx) throws SAXException {
    String characters = content.render(ctx);
    ctx.getContentHandler().characters(characters.toCharArray(), 0, characters.length());
  }

}
