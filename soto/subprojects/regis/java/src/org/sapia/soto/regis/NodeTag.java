package org.sapia.soto.regis;

import java.util.ArrayList;
import java.util.List;

import org.sapia.regis.Node;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class NodeTag implements ObjectCreationCallback, EnvAware{
  
  private Env _env;
  private List _paths = new ArrayList(3);
  
  public void setPath(String path){
    NodeAlias al = new NodeAlias();
    al.setText(path);
    _paths.add(al);
  }
  
  public NodeAlias createPath(){
    NodeAlias al = new NodeAlias();
    _paths.add(al);
    return al;
  }
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public Object onCreate() throws ConfigurationException {
    if(_paths.size() == 0){
      throw new ConfigurationException("Node path not specified");
    }
    
    if(_paths.size() == 1){
      return ((NodeAlias)_paths.get(0)).findNode(_env);
    }
    else{
      CompositeNode comp = new CompositeNode();
      for(int i = 0; i < _paths.size(); i++){
        NodeAlias al = (NodeAlias)_paths.get(i);
        Node n = al.findNode(_env);
        if(n != null){
          comp.addNode(al.getAlias(), n);
        }
      }
      return comp;
    }
  } 
}
