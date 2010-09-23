package org.sapia.resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Abstracts a file resource.
 * 
 * @author Yanick Duchesne
 * 
 */
public interface Resource { 
  /**
   * @return the time, in <code>millis</code>, at which this resource was
   *         last modified.
   */
  public long lastModified();

  /**
   * @return the URI that this resource corresponds to.
   */
  public String getURI();

  /**
   * @return the <code>InputStream</code> corresponding to this resource.
   * @throws IOException
   */
  public InputStream getInputStream() throws IOException;
  
  
  /**
   * @return the <code>Resource</code> corresponding to the given relative URI.
   * @param uri a URI.
   * @throws IOException
   */  
  public Resource getRelative(String uri) throws IOException;
  
}
