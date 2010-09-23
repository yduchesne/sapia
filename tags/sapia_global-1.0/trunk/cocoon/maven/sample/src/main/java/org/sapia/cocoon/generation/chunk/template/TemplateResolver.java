package org.sapia.cocoon.generation.chunk.template;

import java.io.IOException;

import org.apache.cocoon.environment.SourceResolver;
import org.sapia.cocoon.generation.chunk.exceptions.TemplateNotFoundException;
import org.xml.sax.SAXException;

/**
 * This interface specifies template resolving behavior.
 * 
 * @author yduchesne
 *
 */
public interface TemplateResolver {

  public Template resolveTemplate(String uri, SourceResolver sources) 
    throws TemplateNotFoundException, IOException, SAXException;
}
