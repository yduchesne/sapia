package org.sapia.soto.properties;

import java.io.IOException;
import java.io.InputStream;

import org.sapia.resource.Resource;
import org.sapia.soto.util.Utils;
import org.sapia.util.text.TemplateContextIF;

public class PropertyResource implements Resource{
  
  private String _fullUri;
  private Resource _resource;
  private TemplateContextIF _ctx;
  
  PropertyResource(String fullUri, Resource res, TemplateContextIF ctx){
    _fullUri = fullUri;
    _resource = res;
    _ctx = ctx;
    
  }
  
  public InputStream getInputStream() throws IOException {
    return 
        Utils.replaceVars(
          _ctx,
          _resource.getInputStream(),
          _fullUri   
        );
  }
  
  public String getURI() {
    return _fullUri;
  }
  
  public long lastModified() {
    return _resource.lastModified();
  }
  
  public Resource getRelative(String uri) throws IOException {
    return _resource.getRelative(uri);
  }
}
