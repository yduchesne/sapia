package org.sapia.soto.regis;

import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.Property;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.NotFoundException;
import org.sapia.soto.config.SotoIncludeContext;
import org.sapia.soto.util.TemplateContextMap;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class PropertyRefTag 
  implements ObjectCreationCallback, RegistryConsts, EnvAware{

  private static final String SEP = "@";
  private String _name, _nodeRef;
  private Path   _path;
  private boolean _notNull = true;
  private Node _node;
  private Env _env;
  
  public void setEnv(Env env) {
    _env = env;
  }
  
  public void setName(String name){
    int i = name.indexOf(SEP);
    if(i > 0 && i < name.length()-1){
      setPath(name.substring(0, i));
      _name = name.substring(i+1);
    }
    else{
      _name = name;
    }
  }
  
  public void setPath(String path){
    _path = Path.parse(path);
  }
  
  public void setNode(Node node){
    _node = node;
  }
  
  public void setNodeRef(String ref){
    _nodeRef = ref;
  }
  
  public void setNotNull(boolean notNull){
    _notNull = notNull;
  }
  
  public Object onCreate() throws ConfigurationException {
    return getProperty();
  }
  
  protected String getProperty() throws ConfigurationException{
    if(_name == null){
      throw new ConfigurationException("Property name not set");
    }
    Node node = null;
    
    if(_node != null){
      node = _node;
    }
    else if(_nodeRef != null){
      try{
        node = (Node)_env.resolveRef(_nodeRef);
      }catch(NotFoundException e){
        throw new ConfigurationException("Could not resolve Node for: " + _nodeRef, e);
      }
    }
    else{
      node = (Node)SotoIncludeContext.currentTemplateContext().getValue(NODE_REF);
    }
    if(node == null){
      throw new ConfigurationException("Current node not found");
    }
    if(_path != null){
      node = node.getChild(_path);
    }
    if(node == null){
      String path = null;
      if(_path == null) path = _name;
      else path = _path + "/" + _name;
      throw new ConfigurationException("No node found under current node for path: " + path);
    }    
    
    Property prop = node.renderProperty(_name, new TemplateContextMap(SotoIncludeContext.currentTemplateContext()));
    if(prop.isNull() && _notNull){
      throw new ConfigurationException("Missing property: " + _name);
    }
    else{
      String value = prop.asString();
      return value;
    }
  }
  
  protected Env env(){
    return _env;
  }
}
