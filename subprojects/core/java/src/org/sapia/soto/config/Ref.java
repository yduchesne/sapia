/*
 * Ref.java
 *
 * Created on June 28, 2005, 9:01 PM
 */

package org.sapia.soto.config;

import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.NotFoundException;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 *
 * @author yduchesne
 */
public class Ref implements EnvAware, ObjectCreationCallback{
  
  private String _id;
  private Env _env;
  
  /** Creates a new instance of Ref */
  public Ref() {
  }
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public void setId(String id){
    _id = id;
  }
  
  public Object onCreate() throws ConfigurationException{
    if(_id == null){
      throw new ConfigurationException("Reference id not set");
    }
    try{
      return _env.resolveRef(_id);
    }catch(NotFoundException e){
      throw new ConfigurationException("Could not resolve reference: " + _id, e);
    }
  }
  
}
