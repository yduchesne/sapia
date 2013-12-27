package org.sapia.soto.properties;

import java.util.Properties;

import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.NotFoundException;
import org.sapia.soto.util.Utils;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class PropertyRef implements ObjectCreationCallback, EnvAware{

  static final int INDEX_ID = 0;
  static final int INDEX_NAME = 1;
  static final int INDEX_KEY = 2;
  
  private String _id;
  private String _name;
  private String _key;
  private Env _env;
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public void setId(String id){
    _id = id;
  }
  
  public void setName(String name){
    _name = name;
  }
  
  public void setKey(String key){
    _key = key;
  }
  
  public void setPath(String path){
    String[] tokens = Utils.split(path, '/', true);
    if(tokens.length == 3){
      _id = tokens[INDEX_ID];
      _name = tokens[INDEX_NAME];
      _key = tokens[INDEX_KEY];
    }
    else if(tokens.length == 2){
      _name = tokens[INDEX_NAME - 1];
      _key = tokens[INDEX_KEY - 1];      
    }
    else{
      throw new IllegalArgumentException("Invalid path: " + path + "; expected: id/name/key or name/key");
    }
  }
  
  public Object onCreate() throws ConfigurationException {
    return getProperty();
  }
  
  protected String getProperty() throws ConfigurationException{
    if(_name == null){
      throw new ConfigurationException("name not set");
    }
    if(_key == null){
      throw new ConfigurationException("key not set");
    }    
    try{
      PropertyService service;
      if(_id != null){
        service = (PropertyService)_env.lookup(_id);
      }
      else{
        service = (PropertyService)_env.lookup(PropertyService.class);
      }
      Properties props = service.getProperties(Utils.split(_name, ',', true));
      return props.getProperty(_key);
    }catch(NotFoundException e){
      throw new ConfigurationException("Could not resolve property service", e);
    }
  }
  
  protected Env getEnv(){
    return _env;
  }
}
