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
import org.sapia.soto.config.NullObjectImpl;
import org.sapia.soto.config.SotoIncludeContext;
import org.sapia.soto.util.Utils;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 *
 * @author yduchesne
 */
public class ConfigProperty implements EnvAware, ObjectCreationCallback{
  
  static final int CONF = 0;
  static final int CAT = 1;
  static final int PROP = 2;  
  
  protected Env _env;
  protected String _conf, _cat, _default, _path;
  
  /** Creates a new instance of ConfigProperty */
  public ConfigProperty() {
  }
  
  public void setConf(String conf){
    _conf = conf;
  }
  
  public void setCat(String cat){
    _cat = cat;
  }
  
  public void setName(String name){
    _path = name;
  }
  
  public void setPath(String path){
    if(path.length() > 0 && path.charAt(0) == '/'){
      path = path.substring(1);
    }
    String[] parts = Utils.split(path, '/', false);
    if(parts.length != 3){
      throw new IllegalArgumentException("Expected property path with the following format: \n" +
         "configName/categoryName/propertyName; got: " + path);
    }
    _conf = parts[CONF];
    _cat  = parts[CAT];
    _path = parts[PROP];
  }
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public Object onCreate() throws ConfigurationException{  
    String prop = getProperty();
    if(prop == null){
      if(_default != null){
        prop = _default;
      }
      else{
        return new NullObjectImpl();
      }
    }
    return prop;
  }
  
  protected String getProperty() throws ConfigurationException{
    try{
      if(_conf == null){
        throw new ConfigurationException("Configuration name not set");
      }      
      if(_path == null){
        throw new ConfigurationException("Name of configuration property not set");
      }
      ConfigurationService svc  = (ConfigurationService)_env.lookup(ConfigurationService.class);
      String prop = null;
      if(_cat == null){
        prop = svc.getProperty(_conf, _path);
      }
      else{
        prop = svc.getProperty(_conf, _cat, _path);
      }
      if(prop == null){
        try{
          SotoIncludeContext ctx = SotoIncludeContext.currentContext();
          Object val = null;
          if(ctx != null){
            val = ctx.getTemplateContext().getValue(_path);
          }
          if(val != null) prop = val.toString();
        }catch(RuntimeException e){}
      }
      return prop;
    }catch(NotFoundException e){
      throw new ConfigurationException("Could not find configuration service", e);
    }
  }
}
