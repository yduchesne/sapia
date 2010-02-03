package org.sapia.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Specifies the behavior of classes used to resolve resources.
 * 
 * @author Yanick Duchesne
 */
public interface ResourceHandler {
  /**
   * Returns the stream corresponding to the given URI.
   * 
   * @param uri a URI.
   * @return an <code>InputStream</code>.
   * @throws ResourceNotFoundException if a resource corresponding to the
   * URI could does not exist.
   * @throws IOException  if a problem occurs.
   */
  public InputStream getResource(String uri) throws IOException, ResourceNotFoundException;

  /**
   * Returns the resource object corresponding to the given URI.
   * 
   * @param uri
   *          a URI.
   * @return a <code>Resource</code>.
   * @throws IOException
   *           if a problem occurs.
   */
  public Resource getResourceObject(String uri) throws IOException;

  /**
   * Returns <code>true</code> if this handler "recognizes" resources with the
   * given URI. This methods is used to hold resource handlers in a chain of
   * responsability.
   * 
   * @param uri
   *          a URI.
   * @return <code>true</code> if this handler can handle resources with the
   *         given URI.
   */
  public boolean accepts(String uri);
  
  /**
   * @see #accepts(String)
   * 
   * @param uri
   *          a <code>URI</code>.
   * @return <code>true</code> if this handler can handle resources with the
   *         given URI.
   */  
  public boolean accepts(URI uri);

}
