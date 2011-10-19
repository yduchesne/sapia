/*
 * JConfigResource.java
 *
 * Created on August 10, 2005, 2:39 PM
 *
 */

package org.sapia.soto.configuration.jconfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;
import org.sapia.resource.Resource;
import org.sapia.resource.ResourceNotFoundException;

/**
 * This class retrieves JConfig properties as resources.
 * 
 * @author yduchesne
 */
public class JConfigResource implements Resource{
  
  public static final String SCHEME = "jconfig";  
  
  private String _conf;
  private String _cat;
  private String _prop;
  
  /** Creates a new instance of JConfigResource */
  JConfigResource(String conf, String cat, String prop) {
    _conf = conf;
    _cat = cat;
    _prop = prop;
  }

  public InputStream getInputStream() throws IOException {
    Configuration conf = ConfigurationManager.getConfiguration(_conf);
    if(conf == null){
      throw new ResourceNotFoundException(getURI());
    }    
    String value = conf.getProperty(_prop, null, _cat);
    if(conf == null){
      throw new ResourceNotFoundException(getURI());
    }        
    ByteArrayInputStream bis = new ByteArrayInputStream(value.getBytes());
    return bis;
  }

  public long lastModified() {
    return -1;
  }

  public String getURI() {
    return new StringBuffer(SCHEME).append('/').append(_conf)
      .append('/').append(_cat).append('/').append(_prop).toString();
  }
  
  public Resource getRelative(String uri) throws IOException {
    throw new UnsupportedOperationException();
  }
  
}
