package org.sapia.cocoon.generation.chunk.template;

import java.io.IOException;

import org.xml.sax.SAXException;

/**
 * Specifies the behavior of templates.
 * 
 * @author yduchesne
 *
 */
public interface Template {

  public void render(TemplateContext ctx) throws SAXException, IOException;
}
