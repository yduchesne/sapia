package org.sapia.cocoon.generation.chunk.template;

import java.io.IOException;

import org.apache.cocoon.components.modules.input.InputModule;
import org.sapia.cocoon.generation.chunk.exceptions.TemplateNotFoundException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * 
 * @author yduchesne
 *
 */
public interface TemplateContext {

  /**
   * 
   * @param uri
   * @return
   * @throws TemplateNotFoundException
   * @throws SAXException
   * @throws IOException
   */
  public Template resolveTemplate(String uri) throws TemplateNotFoundException, SAXException, IOException;

  /**
   * Parses the content of the resource whose URI is given and parses it as XML.
   * @param uri the URI of a resource to parse.
   * @throws SAXException
   * @throws IOException
   */
  public void include(String uri) throws SAXException, IOException;
  /**
   * @param prefix the prefix (that normally corresponds to the name of an {@link InputModule} of the variable.
   * @param name the name of that the variable (normally: the key of a value in the desired {@link InputModule}).
   * @return the object that was found, or <code>null</code> if none matched.
   */
  public Object getValue(String prefix, String name);
  
  /**
   * @return the {@link ContentHandler} to which templates must emit their SAX events,
   * at rendering time.
   * 
   * @see Template#render(TemplateContext)
   */
  public ContentHandler getContentHandler();
  
  /**
   * @return <code>true</code> if <code>null</code> variables should be tolerated.
   */
  public boolean isLenient();
}
