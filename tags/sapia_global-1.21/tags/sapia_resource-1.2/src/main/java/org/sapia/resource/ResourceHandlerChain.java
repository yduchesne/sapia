package org.sapia.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements a chain of responsibility: an instance of this class
 * holds a list of <code>ResourceHandler</code> instances. The instances are
 * traversed when client application request a specific handler, given a URI.
 * 
 * @author Yanick Duchesne
 */
public class ResourceHandlerChain implements ResourceCapable{

  private List _handlers = new ArrayList();
 
  /**
   * Adds a resource handler to the beginning of the list that this instance
   * holds.
   *  
   * @param handler
   *          a <code>ResourceHandler</code>.
   */
  public synchronized void prepend(ResourceHandler handler) {
    _handlers.add(0, handler);
  }

  /**
   * Adds a resource handler to the end of the list that this instance holds.
   * 
   * @param handler
   *          a <code>ResourceHandler</code>.
   */
  public synchronized void append(ResourceHandler handler) {
    _handlers.add(handler);
  }
  
  public synchronized Resource resolveResource(String uri) 
    throws ResourceNotFoundException, IOException{
    ResourceHandler handler = doSelect(uri);
    if(handler == null){
      throw new ResourceNotFoundException("Could not find resource handler for: " + uri);
    }
    return handler.getResourceObject(uri);
  }

  /**
   * Selects a <code>ResourceHandler</code> from this instance and returns it.
   * The handler is chose based on the given URI. <p/>This instance traverses
   * its handlers, calling the <code>accepts()</code> method on them (the
   * method takes the given URI as a parameter). The first handler that
   * "accepts" the URI (by returning <code>true</code>) is returned to the
   * caller. If no handler as accepted the URI, <code>null</code> is returned.
   * 
   * @param uri
   *          a URI.
   * @return a <b>ResourceHandler </b>, or <code>null</code> if no handler
   *         could be found for the given URI.
   */
  public synchronized ResourceHandler select(String uri) {
    
    return doSelect(uri);
  }
  
  protected ResourceHandler doSelect(String uri){
    ResourceHandler handler = null;
    for(int i = 0; i < _handlers.size(); i++) {
      handler = (ResourceHandler) _handlers.get(i);
      if(handler.accepts(uri)) {
        break;
      }
      else{
        handler = null;
      }
    }
    return handler;    
  }
  
}
