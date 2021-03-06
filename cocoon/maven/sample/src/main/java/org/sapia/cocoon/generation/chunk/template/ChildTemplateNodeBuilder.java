package org.sapia.cocoon.generation.chunk.template;

import org.sapia.cocoon.generation.chunk.Names;
import org.sapia.cocoon.generation.chunk.template.node.ChildTemplateNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * An instance of this class builds a {@link ChildTemplateNode}.
 * 
 * @author yduchesne
 *
 */
public class ChildTemplateNodeBuilder implements Names, ElementNodeBuilder{
  
  public static final String SRC_ATTR_LOCAL_NAME = "src";
  public static final String SRC_ATTR_NAMESPACE = "";

  public Node build(ParserContext ctx, ElementInfo info) throws SAXException{
    String uri = info.getUri();
    String localName = info.getLocalName();
    Attributes atts = info.getAttributes();
    if(uri != null && uri.length() > 0 && uri.equals(NAMESPACE) && localName.equals(CHUNCK_ELEMENT_NAME)){
      String src = atts.getValue(SRC_ATTR_NAMESPACE, SRC_ATTR_LOCAL_NAME);
      if(src == null){
        throw new SAXException(SRC_ATTR_LOCAL_NAME + " attribute not set on " + CHUNCK_ELEMENT_NAME + " element");
      }
      ChildTemplateNode node = new ChildTemplateNode(ctx.getContentParser().parse(src));
      return node;
    }
    else{
      return null;
    }
  }
}
