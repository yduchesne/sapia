package org.sapia.cocoon.generation.chunk.template;

import java.io.IOException;

import org.xml.sax.SAXException;

/**
 * This interface specifies the behavior of objects corresponding to nodes in 
 * an XML document.
 * 
 * @author yduchesne
 *
 */
public interface Node {

  public void render(TemplateContext ctx) throws SAXException, IOException;

}
