package org.sapia.cocoon.generation.chunk.template.node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.sapia.cocoon.generation.chunk.template.ComplexNode;
import org.sapia.cocoon.generation.chunk.template.Node;
import org.sapia.cocoon.generation.chunk.template.TemplateContext;
import org.xml.sax.SAXException;

public abstract class AbstractComplexNode implements ComplexNode{
  
  private List<Node> children = new ArrayList<Node>();

  public void addChild(Node child){
    this.children.add(child);
  }
  
  protected void doRenderChildren(TemplateContext ctx) throws SAXException, IOException{
    for(Node child:this.children){
      child.render(ctx);
    }    
  }

}
