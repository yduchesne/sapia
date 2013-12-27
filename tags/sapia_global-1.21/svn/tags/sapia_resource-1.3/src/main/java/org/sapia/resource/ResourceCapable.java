package org.sapia.resource;

import java.io.IOException;

/**
 * Specifies the behavior of classes that can resolve resources.
 * 
 * @author yduchesne
 *
 */
public interface ResourceCapable {
  
  public Resource resolveResource(String uri) 
    throws ResourceNotFoundException, IOException;

}
 