package org.sapia.cocoon.generation.chunk.template.node;

import org.sapia.cocoon.generation.chunk.template.Node;
import org.sapia.cocoon.generation.chunk.template.TemplateContext;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class LocatorNode implements Node{
  
  private Locator locator;
  
  public LocatorNode(Locator locator) {
    this.locator = locator;
  }
  
  public void render(TemplateContext ctx) throws SAXException {
    ctx.getContentHandler().setDocumentLocator(locator);
  }

}
