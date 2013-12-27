package org.sapia.cocoon.generation.chunk.template;

import java.io.IOException;

import org.xml.sax.SAXException;

/**
 * This class implements the {@link Template} interface over a {@link Node}, which 
 * corresponds to the root of an XML document.
 * 
 * @author yduchesne
 *
 */
public class XMLTemplate implements Template{
  
  private Node root;
  
  public XMLTemplate(Node root) {
    this.root = root;
  }
  
  public void render(TemplateContext ctx) throws SAXException, IOException {
    root.render(ctx);
  }
}
