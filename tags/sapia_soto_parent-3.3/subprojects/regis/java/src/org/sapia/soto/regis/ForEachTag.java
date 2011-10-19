package org.sapia.soto.regis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.sapia.regis.Node;
import org.sapia.soto.Debug;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.config.NullObjectImpl;
import org.sapia.soto.config.SotoIncludeContext;
import org.sapia.soto.util.TemplateContextMap;
import org.sapia.util.text.MapContext;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.NullObject;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class ForEachTag extends BasePropAliasTag implements 
  ObjectCreationCallback, EnvAware, RegistryConsts{
  
  private String _uri;
  private Env _env;
  private Collection _nodes = new ArrayList();
  
  private static final NullObject NULL = new NullObjectImpl();
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public void setUri(String uri){
    _uri = uri;
  }
  
  public void setNodes(Collection nodes){
    _nodes.addAll(nodes);
  }
    
  public Object onCreate() throws ConfigurationException{
    
    Iterator nodes = _nodes.iterator();
    while(nodes.hasNext()){
      Node node = (Node)nodes.next();
      
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
        Debug.debug("<regis:foreach> including resource '" + _uri + "' with node " + ctx.getValue(NODE_PATH));
      }
      _env.include(_uri, ctx);
    }
    return NULL;
  }  

}
