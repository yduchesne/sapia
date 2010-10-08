package org.sapia.regis.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sapia.regis.DuplicateNodeException;
import org.sapia.regis.RWNode;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;

public class RegistryTag extends BaseTag implements ObjectHandlerIF{
  
  private List nodes = new ArrayList();
  private String defaultOperation = NodeTag.OP_UPDATE;
  
  static Class[] ALLOWED = new Class[]{NodeTag.class};
  
  public NodeTag createNode(){
    NodeTag node = new NodeTag();
    nodes.add(node);
    return node;
  }
  
  public void setDefaultOperation(String operation){
    this.defaultOperation = operation;
  }
  
  void create(RWNode root) throws DuplicateNodeException{
    ConfigContext ctx = new ConfigContext(root, new HashMap());
    ctx.setDefaultOperation(defaultOperation);
    for(int i = 0; i < nodes.size(); i++){
      NodeTag nodeCfg = (NodeTag)nodes.get(i);
      nodeCfg.create(ctx);
    }
  }
  
  public void handleObject(String name, Object child) throws ConfigurationException {
    ConfigUtils.checkAllowed(getTagName(), name, child, ALLOWED);
    this.nodes.add(child);
  }

}
