package org.sapia.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Handles resources on the classpath.
 * 
 * <pre>
 * 
 *  ClassPathResourceHandler handler = new ClassPathResourceHandler();
 *  URL url = new URL(handler.getResourceObject(&quot;some/resource/path&quot;).getURI());
 *  ...
 *  
 * </pre>
 * 
 * @author Yanick Duchesne
 */
public class ClasspathResourceHandler implements ResourceHandler, Schemes {

  public ClasspathResourceHandler() {
    super();
  }

  public Resource getResourceObject(String uri) throws IOException {
    URI uriObj = null; 
    if(Utils.hasScheme(uri)){
      try{
        uriObj = new URI(uri);
      }catch(URISyntaxException e){
        throw new IOException(e);
      }
    }
    else{
      try{
        uriObj = new URI(Schemes.SCHEME_RESOURCE+":"+uri);
      }catch(URISyntaxException e){
        throw new IOException(e);
      }
    }
    
    String path = uriObj.getPath() == null ? uriObj.getSchemeSpecificPart() : uriObj.getPath();

    if(path.charAt(0) == '/') {
      path = path.substring(1);
    }

    URL url = null;

    url = getClass().getClassLoader().getResource(path);

    if(url == null) {
      url = Thread.currentThread().getContextClassLoader().getResource(path);
    }

    if(url == null) {
      url = ClassLoader.getSystemResource(path);
    }

    if(url == null) {
      throw new ResourceNotFoundException(path);
    }

    return new UrlResource(url);
  }

  public InputStream getResource(String uri) throws IOException {
    return getResourceObject(uri).getInputStream();
  }

  public boolean accepts(String uri) {
    return doAccepts(Utils.getScheme(uri));    
  }
  
  public boolean accepts(java.net.URI uri) {
    return doAccepts(uri.getScheme());
  }
  
  private boolean doAccepts(String scheme){
    if(scheme == null || scheme.length() == 0 || scheme.equals(SCHEME_RESOURCE)) {
      return true;
    }
    return false;
  }

}
