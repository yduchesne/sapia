package org.sapia.resource;

import java.io.IOException;

/**
 * Thrown when a resource could not be found.
 *
 * @see ResourceHandler
 *
 * @author yduchesne
 */
public class ResourceNotFoundException extends IOException{
  
  /** Creates a new instance of ResourceNotFoundException */
  public ResourceNotFoundException(String msg) {
    super(msg);
  }
  
}
