package org.sapia.soto.spring.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.sapia.soto.util.Utils;
import org.springframework.core.io.Resource;

/**
 * Implements Spring's {@link Resource} interface.
 * 
 * @author yduchesne
 *
 */
public class SpringResource implements Resource{
  
  private org.sapia.resource.Resource _internal;
  
  SpringResource(org.sapia.resource.Resource internal){
    _internal = internal;
  }
  
  public boolean exists() {
    return true;
  }
  
  public Resource createRelative(String name) throws IOException {
    org.sapia.resource.Resource relative = _internal.getRelative(name);
    return new SpringResource(relative);
  }
  
  public String getDescription() {
    return null;
  }
  
  public File getFile() throws IOException {
    String newUri = Utils.chopScheme(_internal.getURI());
    return new File(newUri);
  }
  
  public String getFilename() {
    return _internal.getURI();
  }
  
  public InputStream getInputStream() throws IOException {
    return _internal.getInputStream();
  }
  
  public URL getURL() throws IOException {
    return new URL(_internal.getURI());
  }
  
  public boolean isOpen() {
    return false;
  }
}
