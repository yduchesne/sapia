/*
 * Bind.java
 *
 * Created on June 28, 2005, 8:56 PM
 */

package org.sapia.soto.config;

import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;
import org.sapia.util.xml.confix.ObjectHandlerIF;

/**
 *
 * @author yduchesne
 */
public class Bind implements EnvAware, ObjectCreationCallback, ObjectHandlerIF{
  
  private Env _env;
  private Object _toBind;
  private String _id;
  
  /** Creates a new instance of Bind */
  public Bind() {
  }
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public void setId(String id){
    _id = id;
  }
  
  public Object onCreate() throws ConfigurationException{
    if(_id == null){
      throw new ConfigurationException("Binding ID not set");
    }
    _env.bind(_id, _toBind);
    return _toBind;
  }
  
  public void handleObject(String name, Object o) throws ConfigurationException{
    if(_toBind != null){
      throw new ConfigurationException("Object to bind already set; cannot take multiple children");
    }
    _toBind = o;
  }
  
}
