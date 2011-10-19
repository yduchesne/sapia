package org.sapia.soto.regis;

import org.sapia.regis.Node;
import org.sapia.regis.Property;
import org.sapia.soto.util.TemplateContextMap;
import org.sapia.util.text.SystemContext;
import org.sapia.util.text.TemplateContextIF;

public class NodeTemplateContext implements TemplateContextIF{
  
  private TemplateContextIF _parent;
  private TemplateContextMap _map;
  private Node _node;
  
  public NodeTemplateContext(Node node, TemplateContextIF parent){
    _node = node;
    _parent = parent;
    if(_parent != null){
      _map = new TemplateContextMap(parent);
    }
    else{
      _map = new TemplateContextMap(new SystemContext());
    }
  }
  
  public Object getValue(String name) {
    Property prop = _node.renderProperty(name, _map);
    
    if (prop == null || prop.isNull()) {
      return _map.get(name);
    } else {
      return prop.asString();
    }
  }
  
  public void put(String arg0, Object arg1) {}

}
