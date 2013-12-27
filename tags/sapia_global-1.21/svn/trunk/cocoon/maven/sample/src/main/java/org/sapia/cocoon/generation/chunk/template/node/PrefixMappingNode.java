package org.sapia.cocoon.generation.chunk.template.node;

import java.io.IOException;

import org.sapia.cocoon.generation.chunk.template.TemplateContext;
import org.sapia.cocoon.generation.chunk.template.content.TemplateContent;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class PrefixMappingNode extends AbstractComplexNode{

  private TemplateContent prefix, uri;
  
  public PrefixMappingNode(TemplateContent prefix, TemplateContent uri) {
    this.prefix = prefix;
    this.uri = uri;
  }
  
  public void render(TemplateContext ctx) throws SAXException, IOException {
    ContentHandler handler = ctx.getContentHandler();
    String prefixValue = prefix.render(ctx);
    String uriValue = uri.render(ctx);
    handler.startPrefixMapping(prefixValue, uriValue);
    doRenderChildren(ctx);
    handler.endPrefixMapping(prefixValue);
  }
  
}
