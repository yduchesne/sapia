package org.sapia.cocoon.generation.chunk.template;

import java.util.HashMap;
import java.util.Map;

import org.sapia.cocoon.generation.chunk.Names;
import org.sapia.cocoon.generation.chunk.template.content.TemplateContent;
import org.sapia.cocoon.generation.chunk.template.node.ElementNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Factory class used to create new {@link Node}s.
 * 
 * @author yduchesne
 *
 */
public class NodeFactory {

  private static Map<ElementKey, ElementNodeBuilder> builders = new HashMap<ElementKey, ElementNodeBuilder>();
  private static ElementNodeBuilder DEFAULT = new DefaultElementNodeBuilder();
  
  static{
    builders.put(new ElementKey(Names.NAMESPACE, Names.CHUNCK_ELEMENT_NAME), new ChildTemplateNodeBuilder());
    builders.put(new ElementKey(Names.NAMESPACE, Names.INCLUDE_ELEMENT_NAME), new IncludeNodeBuilder());
  }
  
  /**
   * @param ctx a {@link ParserContext}
   * @param info an {@link ElementInfo}
   * @return a new {@link Node}
   * @throws SAXException
   */
  public static Node createElementNode(ParserContext ctx, ElementInfo info)
    throws SAXException{
    ElementNodeBuilder builder = getBuilderFor(info.getLocalName(), info.getUri());
    Node n = builder.build(ctx, info);
    if(n == null){
      throw new IllegalStateException("Could not build node with: " + builder + "; returned null");
    }
    return n;
  }
  
  private static ElementNodeBuilder getBuilderFor(String localName, String uri){
    ElementKey key = new ElementKey(uri, localName);
    ElementNodeBuilder builder = builders.get(key);
    if(builder == null){
      return DEFAULT;
    }
    else{
      return builder;
    }
  }
  
  static class DefaultElementNodeBuilder implements ElementNodeBuilder{
    
    public Node build(ParserContext ctx, ElementInfo info)
        throws SAXException {
      ElementNode element = new ElementNode(info.getLocalName(), info.getName(), info.getUri());
      Attributes atts = info.getAttributes();
      for(int i = 0; i < atts.getLength(); i++){
        TemplateContent value = ctx.getContentParser().parse(atts.getValue(i));
        Attribute attr = new Attribute(
            atts.getLocalName(i),
            atts.getQName(i),
            atts.getURI(i),
            atts.getType(i),
            value
        );
        element.addAttribute(attr);
      }
      
      return element;
    }
  }
  
  static class ElementKey {
    private String uri, localName;
    
    ElementKey(String uri, String localName){
      this.uri = uri;
      this.localName = localName;
    }
    
    public String getLocalName() {
      return localName;
    }
    
    public String getUri() {
      return uri;
    }
    
    @Override
    public int hashCode() {
      return uri.hashCode() ^ localName.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
      if(obj instanceof ElementKey){
        ElementKey other = (ElementKey)obj;
        return other.uri.equals(uri) && other.localName.equals(localName);
      }
      else{
        return false;
      }
    }
    
  }
}
