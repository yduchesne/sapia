package org.sapia.soto.regis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.NotFoundException;
import org.sapia.soto.util.Param;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class QueryTag implements ObjectCreationCallback, EnvAware{
  
  private Env _env;
  private String _node;
  private List _params = new ArrayList();
  private Collection _nodes;
  
  public void setPath(String node){
    _node = node;
  }
  
  public Param createParam(){
    Param p = new Param();
    _params.add(p);
    return p;
  }
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public Object onCreate() throws ConfigurationException {
    if(_node == null){
      throw new ConfigurationException("Node path not specified");
    }
    
    if(_nodes == null){
      try{
        _nodes = RegistryUtils.getNodes(_node, _params, _env);
      }catch(NotFoundException e){
        throw new ConfigurationException("Could acquire configuration node" + _node, e);      
      }
    }
    return _nodes;
  }  
}
