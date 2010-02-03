package org.sapia.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Implements a <code>Resource</code> over a <code>URL</code>
 * 
 * @see java.net.URL
 * 
 * @author Yanick Duchesne
 */
public class UrlResource implements Resource, URLCapable {
  static final long UNDEFINED = -1;
  private URL       _url;

  public UrlResource(URL url) {
    _url = url;
  }

  public long lastModified() {
    return UNDEFINED;
  }

  public InputStream getInputStream() throws IOException {
    try{
      return _url.openStream();
    }catch(FileNotFoundException e){
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  public String getURI() {
    return _url.toExternalForm();
  }
  
  public URL getURL() throws IOException {
    return _url;
  }

  public String toString() {
    return _url.toExternalForm();
  }
  
  public Resource getRelative(String uri) throws IOException {
    if(Utils.isAbsolute(uri)){
      throw new IOException("URI is absolute: " + uri + "; must be relative");
    }
    if(_url != null){
      return new UrlResource(new URL(Utils.getRelativePath(_url.toExternalForm(), uri, true)));
    }
    else{
      return new UrlResource(new URL(uri));
    }
  }
  
}
