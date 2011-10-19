/*
 * ResourceType.java
 *
 * Created on June 28, 2005, 8:02 PM
 */

package org.sapia.soto.config.types;

import java.io.IOException;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 *
 * @author yduchesne
 */
public class ResourceType implements ObjectCreationCallback, EnvAware{
  
  private String _uri;
  private Env _env;
  
  /** Creates a new instance of ResourceType */
  public ResourceType() {
  }
  
  public void setUri(String uri){
    _uri = uri;
  }
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public Object onCreate() throws ConfigurationException{
    if(_uri == null)
      throw new ConfigurationException("Resource URI not set");
    try{
      return _env.resolveResource(_uri);
    }catch(IOException e){
      throw new ConfigurationException("Could not resolve resource: " + _uri);
    }
  }
}
