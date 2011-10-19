package org.sapia.soto.regis;

import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.NotFoundException;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class BeanTag implements ObjectCreationCallback, EnvAware{
  
  private String _node, _implements;
  private Env _env;
  
  public void setPath(String node){
    _node = node;
    
  }
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public void setImplements(String impl){
    _implements = impl;
  }
  
  public Object onCreate() throws ConfigurationException {
    if(_node == null){
      throw new ConfigurationException("Node path not specified");
    }
    if(_implements == null){
      throw new ConfigurationException("Configuration bean interface not specified");
    }    
    
    Class interf = null;
    try{
      interf = Class.forName(_implements);
    }catch(Exception e){
      throw new ConfigurationException("Could not load configuration interface: " + _implements, e);
    }
    
    try{
      return RegistryUtils.getBean(_node, _env, interf);
    }catch(NotFoundException e){
      throw new ConfigurationException("Could acquire configuration node" + _node, e);      
    }
  }

}
