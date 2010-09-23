package org.sapia.regis;

import java.util.Collection;
import java.util.Map;

/**
 * A instance of this class holds configuration properties (consisting
 * of name/value pairs). It has a composite, recursive structure: it
 * can potentially contain other nodes, and so on.
 * <p>
 * In addition, an instance of this class supports the concepts of
 * links and includes.
 * 
 * @author yduchesne
 *
 */
public interface Node {
  
  public static final String ROOT_NAME = "";
  
  /**
   * @return this instance's name.
   */
  public String getName();

  /**
   * @return the <code>Collection</code> of names of this instance's child nodes.
   */
  public Collection getChildrenNames();
  
  /**
   * @return the type of this node.
   */
  public String getType();
  
  /**
   * @return this instance's absolute <code>Path</code>.
   */
  public Path getAbsolutePath();
  
  /**
   * This method returns a checksum based on the last modification time of this
   * instance and of this parents and links, if any.
   * 
   * @return a <code>long</code> corresponding to a checksum.
   */
  public long lastModifChecksum();
  
  /**
   * @return this instance's parent - or <code>null</code> if this instance is the root.
   */
  public Node getParent();
  
  /**
   * @return <code>true</code> if this instance is the root.
   */
  public boolean isRoot();
  
  /**
   * @return <code>true</code> if this instance inherits its parent node (if any).
   */
  public boolean isInheritsParent();

  /**
   * @param prepended if <code>true</code>, returns this instance's prepended links, else,
   * returns this instance's appended links.
   * @return a <code>Collection</code> of <code>Node</code>s.
   */
  public Collection getLinks(boolean prepended);
  
  /**
   * @return the <code>Collection</code> of <code>Node</code>s that have been included
   * in this instance.
   */
  public Collection getIncludes();  
  
  /**
   * @param key a property key.
   * @return a <code>Property</code> corresponding to the given key (if no corresponding property
   * corresponds to the given key, the returned property's <code>isNull()</code> method returns
   * <code>true</code>).
   */
  public Property getProperty(String key);
  
  /**
   * This method returns the property corresponding to the given key. If the
   * property's value holds a variable or many variables (in the ${varName} notation), these
   * variables are rendered prior to the property being returned - the client application
   * will thus not "see" the variables.
   * <p>
   * Variable values are search as follows:
   * <ul>
   *   <li>First, if this instance inherits from its parent, the variable is  
   * </ul>
   * <p>
   * If any variable could not be rendered, then it is left in its original notation.
   * 
   * @param key a property key.
   * @return a <code>Property</code> corresponding to the given key (if no corresponding property
   * corresponds to the given key, the returned property's <code>isNull()</code> method returns
   * <code>true</code>).
   */
  public Property renderProperty(String key);
  
  /**
   * @param keys
   * @return
   */
  //public Collection renderProperties(Collection keys);
  
  /**
   * @param values a <code>Map</code> of properties (name/value pairs) to be used internally
   * by this method's interpolation logic.
   * @see #renderProperty(String)
   */
  public Property renderProperty(String key, Map values);
  
  /**
   * @return the <code>Collection</code> of keys of the properties held by this instance.
   */
  public Collection getPropertyKeys();  
  
  /**
   * Returns this instance's properties. Variable interpolation is performed prior to returning the
   * properties.
   * 
   * @return the <code>Map</code> holding a copy of the configuration properties held by this instance.
   */
  public Map getProperties();
  
  /**
   * @param values a <code>Map</code> of properties (name/value pairs) to be used internally
   * by this method's interpolation logic.
   * @see #getProperties()
   */
  public Map getProperties(Map values);  
  
  /**
   * @param name the name of the child to acquire.
   * @return the child <code>Node</code>, or <code>null</code> if no such child exists.
   */
  public Node getChild(String name);
  
  /** 
   * @param path the <code>Path</code> corresponding to the child to create.
   * @return the child <code>Node</code>, or <code>null</code> if no such child exists.
   */
  public Node getChild(Path path);
  
  /**
   * @return the <code>Collection</code> of child <code>Node</code>s of this instance.
   */
  public Collection getChildren();
  
  /**
   * @param query a <code>Query</code>
   * @return a <code>Collection</code> of <code>Node</code>s that match
   * the given query.
   */
  public Collection getNodes(Query query);
  
}
