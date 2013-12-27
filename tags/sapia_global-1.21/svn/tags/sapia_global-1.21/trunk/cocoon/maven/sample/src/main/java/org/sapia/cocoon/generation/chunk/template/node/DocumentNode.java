package org.sapia.cocoon.generation.chunk.template.node;

import java.io.IOException;

import org.sapia.cocoon.generation.chunk.template.TemplateContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class DocumentNode extends AbstractComplexNode{

  public void render(TemplateContext ctx) throws SAXException, IOException {
    ContentHandler handler = ctx.getContentHandler();
    handler.startDocument();
    doRenderChildren(ctx);
    handler.endDocument();    
  }
}
