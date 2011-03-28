package org.sapia.soto.properties.example;

import java.io.IOException;
import java.util.Properties;

import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.util.Utils;

public class PropertyExample implements EnvAware{

  private String property;
  private Properties properties;
  private String content;
  private Env env;
  
  public void setEnv(Env env) {
    this.env = env;
  }
  public Properties getProperties() {
    return properties;
  }
  public void setProperties(Properties properties) {
    this.properties = properties;
  }
  public String getProperty() {
    return property;
  }
  public void setProperty(String property) {
    this.property = property;
  }
  public void setUri(String uri) throws IOException{
    content = Utils.textStreamToString(env.resolveStream(uri));
  }
  public String getContent(){
    return content;
  }
  
}
