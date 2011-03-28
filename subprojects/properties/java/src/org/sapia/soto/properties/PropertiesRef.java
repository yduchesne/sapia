package org.sapia.soto.properties;

import org.sapia.soto.Debug;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.NotFoundException;
import org.sapia.soto.util.Utils;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class PropertiesRef implements ObjectCreationCallback, EnvAware{

  static final int INDEX_ID = 0;
  static final int INDEX_NAME = 1;
  
  private String _id;
  private String _name;
  private Env _env;
  private boolean _notNull;
  private Object _default;
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public void setId(String id){
    _id = id;
  }
  
  public void setName(String name){
    _name = name;
  }
  
  public void setNotNull(boolean notNull){
    _notNull = notNull;
  }
  
  public void setDefault(Object def){
    _default = def;
  }
  
  public void setPath(String path){
    String[] tokens = Utils.split(path, '/', true);
    if(tokens.length == 2){
      _id = tokens[INDEX_ID];
      _name = tokens[INDEX_NAME];
    }
    else if(tokens.length == 1){
      _name = tokens[INDEX_NAME - 1];
    }
    else{
      throw new IllegalArgumentException("Invalid path: " + path + "; expected: id/name or name");
    }
  }  
  
  public Object onCreate() throws ConfigurationException {
    if(_name == null){
      throw new ConfigurationException("name not set");
    }
    try{
      PropertyService service;
      if(_id != null){
        service = (PropertyService)_env.lookup(_id);
      }
      else{
        service = (PropertyService)_env.lookup(PropertyService.class);
      }
      Object prop = service.getProperties(Utils.split(_name, ',', true));
      if(prop == null){
        if(_default != null){
          if(Debug.DEBUG){
            Debug.debug(getClass(), "Assigning default for: " + _name  + " - " + _default);
          }
          prop = _default;
        }
        else if(_notNull) {
          throw new ConfigurationException("Missing property: " + _name + "; cannot be null");
        }
      }
      if(prop == null && Debug.DEBUG){
        Debug.debug(getClass(), "Property " + _name + " evaluates to null");
      }
      return prop;
    }catch(NotFoundException e){
      throw new ConfigurationException("Could not resolve property service", e);
    }
  }

}
