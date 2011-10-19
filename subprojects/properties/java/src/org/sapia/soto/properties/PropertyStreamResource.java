package org.sapia.soto.properties;

import java.io.IOException;
import java.io.InputStream;

import org.sapia.resource.Resource;

public class PropertyStreamResource implements Resource{
  
  private PropertyService _props;
  private String _uri;
  private String[] _names;
  private Resource _parent;
  
  public PropertyStreamResource(String uri, Resource parent, String[] names, PropertyService props) {
    _uri = uri;
    _names = names;
    _props = props;
    _parent = parent;
  }
  
  public InputStream getInputStream() throws IOException {
    return _props.getPropertiesStream(_names);
  }
  
  public long lastModified() {
    return 0;
  }
  
  public String getURI() {
    return _uri;
  }
  
  public Resource getRelative(String uri) throws IOException {
    if(_parent == null){
      throw new UnsupportedOperationException();
    }
    
    return _parent.getRelative(uri);
  }

}
