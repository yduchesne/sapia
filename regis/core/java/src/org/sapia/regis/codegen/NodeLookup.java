package org.sapia.regis.codegen;

import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.Registry;

/**
 * A utility class that can be used to lookup instances of generated classes in
 * a registry.
 * 
 * @see #create(Registry)
 * @see #getInstanceFor(Class)
 * 
 * @author yduchesne
 *
 */
public class NodeLookup {
  
  private Node root;
  
  private NodeLookup(Node root) {
    this.root = root;
  }
  
  /**
   * Use this method to create an instance of this class.
   * 
   * @param registry the {@link Registry} to use to perform lookups. 
   * @return a {@link NodeLookup}
   */
  public static NodeLookup create(Registry registry){
    NodeLookup lookup = new NodeLookup(registry.getRoot());
    return lookup;
  }
  
  @SuppressWarnings(value="unchecked")
  public <T extends NodeCapable> T getInstanceFor(Class<T> nodeClass){ 
    return (T)getRawInstanceFor(nodeClass);
  }
  
  /**
   * @param nodeClass a {@link Class} object.
   * @return the {@link NodeCapable} instance that was instantiated.
   */
  public Object getRawInstanceFor(Class<?> nodeClass){
    try{
      return  nodeClass.getConstructor(new Class[]{Node.class}).newInstance(getNodeFor(nodeClass));
    }catch(Exception e){
      throw new IllegalStateException("Could not invoke constructor on class " + nodeClass, e);
    }
  }
  
  public Node getNodeFor(Class<?> nodeClass){
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
    Node child = root.getChild(Path.parse(path));
    if(child == null){
      throw new IllegalStateException("No node found for " + path);
    }
    return child;
  }
  
}

