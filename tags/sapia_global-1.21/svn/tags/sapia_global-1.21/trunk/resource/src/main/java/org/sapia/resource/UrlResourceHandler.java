package org.sapia.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * Implements a <code>ResourceHandler</code> that produces
 * <code>UrlResource</code> instances.
 * 
 * @see org.sapia.resource.UrlResource
 * 
 * @author Yanick Duchesne

 */
public class UrlResourceHandler implements ResourceHandler {
  public UrlResourceHandler() {
    super();
  }

  public Resource getResourceObject(String uri) throws IOException {
    if(uri.startsWith(Schemes.SCHEME_RESOURCE)){
      return new ClasspathResourceHandler().getResourceObject(uri);
    }
    else{
      return new UrlResource(new URL(uri));
    }
  }

  public InputStream getResource(String uri) throws IOException {
    return getResourceObject(uri).getInputStream();
  }

  public boolean accepts(String uri) {
    return doAccepts(Utils.getScheme(uri));
  }
  
  public boolean accepts(URI uri) {
    return doAccepts(uri.getScheme());
  }
  
  private boolean doAccepts(String scheme){
    if(scheme == null || scheme.length() == 0) {
      return false;
    }
    return true;    
  }

}
