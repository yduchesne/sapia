package org.sapia.soto.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public interface PropertyService {
  
  public Properties getProperties(String name);
  
  public Properties getProperties(String[] names);  
  
  public InputStream getPropertiesStream(String name) throws IOException;
  
  public InputStream getPropertiesStream(String[] names) throws IOException;  
}
