package org.sapia.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implements the <code>Resource</code> interface over a string.
 * 
 * @author Yanick Duchesne
 */
public class StringResource implements Resource {

  private String _string, _uri;

  public StringResource(String uri, String str) {
    _string = str;
    _uri = uri;
  }

  public long lastModified() {
    return -1;
  }

  public InputStream getInputStream() throws IOException {
    return new ByteArrayInputStream(_string.getBytes());
  }

  public String getURI() {
    return _uri;
  }

  /**
   * This method throws an <code>UnsupportedOperationException</code>.
   */
  public Resource getRelative(String uri) throws IOException {
    throw new UnsupportedOperationException();
  }
}
