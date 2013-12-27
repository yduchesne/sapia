package org.sapia.cocoon.generation.chunk.template.node;

import org.sapia.cocoon.generation.chunk.template.Node;
import org.sapia.cocoon.generation.chunk.template.TemplateContext;
import org.sapia.cocoon.generation.chunk.template.content.TemplateContent;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class ProcessingInstructionNode implements Node{

  private TemplateContent target, data;
  
  public ProcessingInstructionNode(TemplateContent target, TemplateContent data) {
    this.target = target;
    this.data = data;
  }
  
  public void render(TemplateContext ctx) throws SAXException {
    ContentHandler handler = ctx.getContentHandler();
    handler.processingInstruction(target.render(ctx), data.render(ctx));
  }
  
 
}
