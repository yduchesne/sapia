package org.sapia.soto.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.NotFoundException;
import org.sapia.soto.config.SotoIncludeContext;
import org.sapia.soto.util.Param;
import org.sapia.soto.util.PropertiesContext;
import org.sapia.soto.util.Utils;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class Include implements ObjectCreationCallback, EnvAware{

  static final int INDEX_ID = 0;
  static final int INDEX_NAME = 1;
  
  private String _uri;
  private List _params = new ArrayList();
  private String _id;
  private String _name;
  private Env _env;
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public void setUri(String uri){
    _uri = uri;
  }
  
  public void setId(String id){
    _id = id;
  }
  
  public void setName(String name){
    _name = name;
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

  public Param createParam(){
    Param p = new Param();
    _params.add(p);
    return p;
  }
  
  public Object onCreate() throws ConfigurationException{
    if(_name == null){
      throw new ConfigurationException("name not set");
    }
    PropertyService service;    
    try{
      if(_id != null){
        service = (PropertyService)_env.lookup(_id);
      }
      else{
        service = (PropertyService)_env.lookup(PropertyService.class);
      }
    }catch(NotFoundException e){
      throw new ConfigurationException("Could not resolve property service", e);
    }
    
    Properties props = service.getProperties(Utils.split(_name, ',', true));

    for(int i = 0; i < _params.size(); i++){
      Param p = (Param)_params.get(i);
      if(p.getName() != null && p.getValue() != null){
        props.setProperty(p.getName(), p.getValue().toString());
      }
    }
    
    PropertiesContext ctx = new PropertiesContext(SotoIncludeContext.currentTemplateContext(), false);
    ctx.addProperties(props);
    return _env.include(_uri, ctx);
  }
}
