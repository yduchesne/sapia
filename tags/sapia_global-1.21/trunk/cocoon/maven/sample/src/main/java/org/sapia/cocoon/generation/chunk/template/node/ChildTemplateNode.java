package org.sapia.cocoon.generation.chunk.template.node;

import java.io.IOException;

import org.sapia.cocoon.generation.chunk.template.Node;
import org.sapia.cocoon.generation.chunk.template.Template;
import org.sapia.cocoon.generation.chunk.template.TemplateContext;
import org.sapia.cocoon.generation.chunk.template.content.TemplateContent;
import org.xml.sax.SAXException;

public class ChildTemplateNode extends AbstractComplexNode{
  
  private TemplateContent uri;
  
  public ChildTemplateNode(TemplateContent uri){
    this.uri = uri;
  }
  
  @Override
  public void addChild(Node child){}
  
  public void render(TemplateContext ctx) throws SAXException, IOException {
    Template t = ctx.resolveTemplate(uri.render(ctx));
    t.render(ctx);
  }
  

}
