package org.sapia.soto.me.net;

import java.io.IOException;

/**
 * Thrown when a resource could not be found.
 */
public class ResourceNotFoundException extends IOException {

  /**
   * Creates a new ResourceNotFoundException instance.
   * 
   * @param aMessage The description of the exception.
   */
  public ResourceNotFoundException(String aMessage) {
    super(aMessage);
  }
}
