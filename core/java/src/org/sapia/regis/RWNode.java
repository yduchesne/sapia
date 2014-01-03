package org.sapia.regis;

/**
 * This insterface extends the <code>Node</code> interface in 
 * order to support write operations.
 * 
 * @author yduchesne
 *
 */
public interface RWNode extends Node{
  
  /**
   * @param key a property key.
   * @param value a property value.
   */
  public void setProperty(String key, String value);
  
  /**
   * @param key the key of the property to delete.
   */
  public void deleteProperty(String key);
  
  /**
   * Deletes this instance's properties.
   */
  public void deleteProperties();
  
  /**
   * @param type the type of this node.
   * 
   * @see org.sapia.regis.forms.Form#getType()
   */
  public void setType(String type);
  /**
   * @param name the name under which the new child node should be bound under this instance.
   * @return the newly created <code>Node</code>
   * @throws DuplicateNodeException if a node already exists under this instance,
   * for the given name.
   */
  public Node createChild(String name) throws DuplicateNodeException;
  
  /**
   * @param name deletes the child <code>Node</code> with the given name from this instance.
   */
  public void deleteChild(String name);

  /**
   * Deletes the child <code>Node</code>s of this instance.
   */
  public void deleteChildren();
  
  /**
   * Sets the inheritance mode of this instance.
   * 
   * @param inheritsParent if <code>true</code>, indicates to this instance that it should
   * inherit its parent's configuration properties.
   */
  public void setInheritsParent(boolean inheritsParent);
  
  /**
   * @param node a linked <code>Node</code>.
   */
  public void prependLink(Node node);
  
  /**
   * @param node a linked <code>Node</code>
   */
  public void removePrependedLink(Node node);
  
  /**
   * @param node a linked <code>Node</code>.
   */
  public void appendLink(Node node); 
  
  /**
   * @param node a linked <code>Node</code>
   */
  public void removeAppendedLink(Node node);
  
  /**
   * Removes all linked nodes from this instance.
   */
  public void deleteLinks();
  
  /**
   * @param node a <code>Node</code> to included.
   */
  public void addInclude(Node node);
  
  /**
   * @param node a <code>Node</code> to remove from
   * this instance's included nodes.
   */
  public void removeInclude(Node node);
  
  /**
   * Removes all included nodes from this instance.
   */
  public void deleteIncludes();

  /**
   * Moves this instance to the given parent.
   * @param newParent a <code>Node</code>.
   */
  public void moveTo(Node newParent);  

}
