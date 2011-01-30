package org.sapia.regis.loader;

import java.util.HashMap;
import java.util.Map;

import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.RWNode;

public class ConfigContext {
  
  private Map nodes = new HashMap();
  private RWNode parent;
  private String defaultOperation;
  
  public ConfigContext(RWNode parent, Map nodes){
    this.parent = parent;
    this.nodes = nodes;
  }
  
  public Map getNodes(){
    return nodes;
  }
  
  public RWNode getParent(){
    return parent;
  }

  public String getDefaultOperation() {
    return defaultOperation;
  }

  public void setDefaultOperation(String defaultOperation) {
    this.defaultOperation = defaultOperation;
  }
  
  public Node getNodeFor(String idOrPath, boolean isPath){
    if(isPath){
      Node root = getRoot();
      return root.getChild(Path.parse(idOrPath));
    }
    else{
      return (Node)nodes.get(idOrPath);
    }
  }
  
  private Node getRoot(){
    Node current = parent;
    
    while(!current.isRoot()){
      current = current.getParent();
    }
    return current;
  }

}
