package org.sapia.soto.spring.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.springframework.core.io.Resource;

public class NullSpringResource implements Resource{
  
  private String _name;
  
  public NullSpringResource(String name) {
    _name = name;
  }
  
  public Resource createRelative(String name) throws IOException {
    if(_name.endsWith("/") || _name.endsWith("\\")){
      return new NullSpringResource(_name+"/"+name);
    }
    else{
      return new NullSpringResource(_name + name);
    }
  }
  
  public boolean exists() {
    return false;
  }
  
  public String getDescription() {
    return null;
  }
  
  public File getFile() throws IOException {
    throw new FileNotFoundException(_name);
  }
  
  public String getFilename() {
    return _name;
  }
  
  public InputStream getInputStream() throws IOException {
    throw new FileNotFoundException(_name);
  }
  
  public URL getURL() throws IOException {
    return new URL(_name);
  }
  
  public boolean isOpen() {
    return false;
  }

}
