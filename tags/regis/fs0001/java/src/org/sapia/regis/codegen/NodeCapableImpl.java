package org.sapia.regis.codegen;

import org.sapia.regis.Node;
import org.sapia.regis.Path;

/**
 * This class serves as the base class of all generated classes.
 * @author yduchesne
 *
 */
public class NodeCapableImpl implements NodeCapable{
  
  protected Node node;
  
  public NodeCapableImpl(Node node){
    this.node = node;
  }
  
  public Node getNode(){
    return node;
  }
  
  protected <T> T getInstanceFor(Class<T> clazz, String nodeName){
    Node n = node.getChild(nodeName);
    if(n == null){
      throw new IllegalStateException("Could not find node for: " + clazz + "; node: " + nodeName);
    }
    try{
      return clazz.getConstructor(new Class[]{Node.class}).newInstance(new Object[]{n});
    }catch(Exception e){
      throw new IllegalStateException("Could not create instance for: " + clazz + "; node: " + nodeName);
    }
  }
  
  public <T extends NodeCapable> T getInstanceFor(Class<T> nodeClass){
    String path = null;
    try{
      path = (String)nodeClass.getField("NODE_PATH").get(null);
    }catch(NoSuchFieldException e){
      throw new IllegalStateException("Could not find NODE_PATH field on class " + nodeClass, e);
    }catch(Exception e){
      throw new IllegalStateException("Could not access NODE_PATH field on class " + nodeClass, e);
    }
    if(path == null){
      throw new IllegalStateException("Static NODE_PATH field is null on class " + nodeClass);
    }
    Node child = node.getChild(Path.parse(path));
    
    if(child == null){
      throw new IllegalStateException("Could not find node for: " + nodeClass + "; node: " + path);
    }
    
    try{
      return  nodeClass.getConstructor(new Class[]{Node.class}).newInstance(child);
    }catch(Exception e){
      throw new IllegalStateException("Could not invoke constructor on class " + nodeClass, e);
    }
  }  

}
