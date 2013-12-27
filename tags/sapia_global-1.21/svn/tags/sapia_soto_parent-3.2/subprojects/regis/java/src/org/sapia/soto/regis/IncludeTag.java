package org.sapia.soto.regis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sapia.regis.Node;
import org.sapia.soto.Debug;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.config.SotoIncludeContext;
import org.sapia.util.text.MapContext;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class IncludeTag extends BasePropAliasTag implements 
  ObjectCreationCallback, EnvAware, RegistryConsts{
  
  private String _uri;
  private Env _env;
  private List _paths = new ArrayList(3);
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public void setUri(String uri){
    _uri = uri;
  }
  
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
  
  public Object onCreate() throws ConfigurationException{
    if(_paths.size() == 0){
      throw new IllegalStateException("Missing node path");
    }
    
    Node node = null;
    if(_paths.size() == 1){
      NodeAlias al = (NodeAlias)_paths.get(0);
      node = al.findNode(_env);
    }
    else{
      CompositeNode comp = new CompositeNode();
      for(int i = 0; i < _paths.size(); i++){
        NodeAlias al = (NodeAlias)_paths.get(i);
        Node child = al.findNode(_env);
        if(child != null){
          comp.addNode(al.getAlias(), child);
        }
      }
      node = comp;
    }
    
    HashMap props = new HashMap();
    MapContext ctx = new MapContext(props, new NodeTemplateContext(node, SotoIncludeContext.currentTemplateContext()), false);

    for(int i = 0; i < _params.size(); i++){
      PropertyAlias p = (PropertyAlias)_params.get(i);
      p.setProperty(node, props);
    }
   
    props.put(NODE_PATH, node.getAbsolutePath().toString());
    props.put(NODE_NAME, node.getName());
    props.put(NODE_REF, node);

    if (Debug.DEBUG) {
      Debug.debug("<regis:include> including resource '" + _uri + "' with node " + ctx.getValue(NODE_PATH));
    }
    
    return _env.include(_uri, ctx);
  }  

}
