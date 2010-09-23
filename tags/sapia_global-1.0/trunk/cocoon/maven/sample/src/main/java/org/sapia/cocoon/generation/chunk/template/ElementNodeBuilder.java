package org.sapia.cocoon.generation.chunk.template;

import org.xml.sax.SAXException;

/**
 * This interface specifies the behavior of classes that build
 * {@link Node} instances corresponding to XML elements.
 * 
 * @author yduchesne
 *
 */
public interface ElementNodeBuilder {

  public Node build(ParserContext ctx, ElementInfo info) throws SAXException;
  
}
