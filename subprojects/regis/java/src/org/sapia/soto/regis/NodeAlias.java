package org.sapia.soto.regis;

import org.sapia.regis.Node;
import org.sapia.soto.Env;
import org.sapia.soto.NotFoundException;
import org.sapia.util.xml.confix.ConfigurationException;

public class NodeAlias {

  private String _path, _alias;
  private boolean _notNull = true;
  
  public void setAlias(String alias){
    _alias = alias;
  }
  
  public String getAlias(){
    return _alias;
  }
  
  public void setPath(String path){
    setText(path);
  }
  
  public String getPath(){
    return _path;
  }
  
  public void setText(String path){
    _path = path;
  }
  
  public void setNotNull(boolean notNull){
    _notNull = notNull;
  }
  
  public boolean isNotNull(){
    return _notNull;
  }
  
  Node findNode(Env env) throws ConfigurationException{
    Node node = null;
    if(_path == null){
      throw new ConfigurationException("Node path not specified");
    }
    try{
      node = RegistryUtils.getNode(_path, env);
    }catch(NotFoundException e){
      throw new ConfigurationException("Could acquire configuration node" + _path, e);      
    }
    if(node == null && _notNull){
      throw new ConfigurationException("No configuration node found for: " + _path);
    }
    return node;
  }
  
}
