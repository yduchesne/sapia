package org.sapia.soto.me.net;

import java.io.IOException;
import java.io.InputStream;

/**
 * Abstracts a resource.
 * 
 */
public interface Resource { 

  /**
   * Returns the URI of this resource.
   * 
   * @return The URI of this resource.
   */
  public String getURI();

  /**
   * Returns an input stream over this resource.
   * 
   * @return An {@link java.io.InputStream} over this resource. 
   * @throws IOException If an error occurs getting the input stream.
   */
  public InputStream getInputStream() throws IOException;
  
  /**
   * Returns a new resource from this relatove resource based on
   * the root resource passed in.
   *  
   * @param aRootResource The base resource to define the absolute resource.
   * @return The created absolute resource.
   */
  public Resource toAbsoluteFrom(Resource aRootResource);
}
