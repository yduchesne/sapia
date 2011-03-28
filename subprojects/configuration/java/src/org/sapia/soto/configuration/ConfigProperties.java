/*
 * ConfigProperty.java
 *
 * Created on August 10, 2005, 12:54 PM
 *
 */

package org.sapia.soto.configuration;

import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.NotFoundException;
import org.sapia.util.xml.confix.ConfigurationException;

/**
 *
 * @author yduchesne
 */
public class ConfigProperties implements EnvAware{
  
  private Env _env;
  private String _conf, _cat;
  
  /** Creates a new instance of ConfigProperty */
  public ConfigProperties() {
  }
  
  public void setConf(String conf){
    _conf = conf;
  }
  
  public void setCategory(String cat){
    _cat = cat;
  }
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public Object onCreate() throws ConfigurationException{
    try{
      if(_conf == null){
        throw new ConfigurationException("Configuration name not set");
      }      
      if(_cat == null){
        throw new ConfigurationException("Category of properties not set");
      }
      ConfigurationService svc  = (ConfigurationService)_env.lookup(ConfigurationService.class);
      return svc.getProperties(_conf, _cat);
    }catch(NotFoundException e){
      throw new ConfigurationException("Could not find configuration service", e);
    }
  }
}
