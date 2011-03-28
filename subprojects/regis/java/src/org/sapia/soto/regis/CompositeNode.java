package org.sapia.soto.regis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.Property;
import org.sapia.regis.Query;
import org.sapia.regis.impl.PropertyImpl;

/**
 * An instance of this class is "composed" of other <code>Node</code>s. In addition,
 * its property access methods delegate property lookup to the child nodes, sequentially - read
 * the javadoc further below for more detail.
 * 
 * @author yduchesne
 *
 */
public class CompositeNode implements Node{
  
  static final String NAME = "";
  static final Path PATH = Path.parse(NAME);
  private List _nodes = new ArrayList();
  
  /**
   * 
   * @param alias an alias under which to bind the given node under this instance, or
   * <code>null</code> if the given node should be bound under its actual name.
   * @param node a <code>Node</code> to bind to this instance.
   */
  public void addNode(String alias, Node node){
    _nodes.add(0, new NodeWrapper(alias, node));
  }

  public String getName() {
    return NAME;
  }
  
  public Path getAbsolutePath() {
    return PATH;
  }
  
  /**
   * @return the child <code>Node</code> under the given path, or <code>null</code>
   * if no such node exists.
   */
  public Node getChild(Path path) {
    if(path == null || path.isRoot()){
      return this;
    }
    Iterator tokens = path.tokens();
    NodeWrapper child = find((String)tokens.next());
    if(child != null){
      return child.node().getChild(copyPath(tokens));
    }
    return null;
  }

  /**
   * @return #getChild(Path)
   */
  public Node getChild(String name) {
    if(name == null || name.equals(NAME)){
      return this;
    }
    return ((NodeWrapper)find(name)).node();
  }

  /**
   * @return this instance's children (i.e.: the nodes that were
   * added to it).
   * 
   * @see #addNode(String, Node)
   */
  public Collection getChildren() {
    List toReturn = new ArrayList(_nodes.size());
    for(int i = 0; i < _nodes.size(); i++){
      NodeWrapper node = (NodeWrapper)_nodes.get(i);
      toReturn.add(node.node());
    }
    return toReturn;
  }
  
  /**
   * @return the names of this instance's children (i.e.: the nodes that were
   * added to it).
   */
  public Collection getChildrenNames() {
    List toReturn = new ArrayList(5);
    for(int i = 0; i < _nodes.size(); i++){
      NodeWrapper node = (NodeWrapper)_nodes.get(i);
      toReturn.add(node.node().getName());
    }    
    return toReturn;
  }

  /**
   * Empty implementation (returns an empty collection).
   */
  public Collection getIncludes() {
    return Collections.EMPTY_LIST;
  }

  /**
   * Empty implementation (returns an empty collection).
   */
  public Collection getLinks(boolean prepended) {
    return Collections.EMPTY_LIST;
  }

  /**
   * @param query a <code>Query</code>
   * @return the <code>Collection</code> of <code>Node</code>s that match the given
   * query.
   */
  public Collection getNodes(Query query) {
    if(query.getPath() == null){
      query.setPath("");
    }
    Iterator itr = query.getPath().tokens();
    if(itr.hasNext()){
      String name = (String)itr.next();
      Node child = getChild(name);
      if(child == null){
        return Collections.EMPTY_LIST;
      }
      return child.getNodes(copyQuery(itr, query));
    }
    else{
      Map crit = query.getCriteria();
      List result = new LinkedList();
      Iterator children = getChildren().iterator();
      while(children.hasNext()){
        Node child = (Node)children.next();
        boolean matches = true;
        Iterator propNames = crit.keySet().iterator();      
        while(propNames.hasNext()){
          String name = (String)propNames.next();
          String val = (String)crit.get(name);
          Property prop = child.getProperty(name);
          if(prop.isNull()){
            matches = false;
            break;
          }
          else if(prop.getValue().equals(val)){
            continue;
          }
          else{
            matches = false;
            break;
          }
        }
        if(matches){
          result.add(child);
        }
      }
      return result;
    }
  }

  /**
   * This method returns <code>null</code>.
   */
  public Node getParent() {
    return null;
  }

  /**
   * @see #getProperties()
   */
  public Map getProperties() {
    return doGetProperties(Collections.EMPTY_MAP);
  }

  /**
   * This method returns the aggregated properties of its child nodes, starting with
   * the one that was added first - meaning that the properties of the last nodes override
   * the ones of the first nodes, for properties of equal names.
   */
  public Map getProperties(Map values) {
    return doGetProperties(values);
  }

  /**
   * This method internally iterates over this instance's child nodes, starting from the one
   * that was added last. It attempts to find the property from each node over which it
   * iterates. The first property that is not null is returned. Otherwise, search continues
   * until all nodes have been searched.
   * 
   * @param name a property name.
   * @return the <code>Property</code> corresponding to the given name.
   */
  public Property getProperty(String name) {
    for(int i = 0; i < _nodes.size(); i++){
      Node n = ((NodeWrapper)_nodes.get(i)).node();
      Property prop = n.getProperty(name);
      if(!prop.isNull()){
        return prop;
      }
    }
    return new PropertyImpl(name, null);
  }

  /**
   * This method returns the property names of its child nodes.
   */
  public Collection getPropertyKeys() {
    Set keys = new TreeSet();
    for(int i = 0; i < _nodes.size(); i++){
      Node n = ((NodeWrapper)_nodes.get(i)).node();
      keys.addAll(n.getPropertyKeys());
    }
    return keys;
  }

  public String getType() {
    return null;
  }

  public boolean isInheritsParent() {
    return false;
  }

  public boolean isRoot() {
    return true;
  }

  public long lastModifChecksum() {
    return 0;
  }
  
  /**
   * @see #getProperty(String)
   */
  public Property renderProperty(String key, Map values) {
    for(int i = 0; i < _nodes.size(); i++){
      Node n = ((NodeWrapper)_nodes.get(i)).node();
      Property p = n.getProperty(key);
      if(!p.isNull()){
        return n.renderProperty(key, values);
      }
    }
    return null;
  }

  /**
   * @see #getProperty(String)
   */
  public Property renderProperty(String key) {
    return renderProperty(key, Collections.EMPTY_MAP);
  }
  
  private Path copyPath(Iterator tokens){
    List tokenList = new ArrayList();
    while(tokens.hasNext()){
      tokenList.add(tokens.next());
    }
    Path p = new Path(tokenList);
    return p;
  }
  
  private Query copyQuery(Iterator tokens, Query src){
    Path p = copyPath(tokens);
    Query q = new Query();
    q.setPath(p);
    Map criteria = src.getCriteria();
    Iterator keys = criteria.keySet().iterator();
    while(keys.hasNext()){
      String key = (String)keys.next();
      String value = (String)criteria.get(key);
      if(value != null){
        q.addCrit(key, value);
      }
    }
    return q;
  }  
  
  private Map doGetProperties(Map values){
    Map result = new HashMap();

    for(int i = _nodes.size() - 1; i >= 0; i--){
      Node child = ((NodeWrapper)_nodes.get(i)).node();
      result.putAll(child.getProperties(values));
    }
    return result;
  }
  
  private NodeWrapper find(String name){
    for(int i = 0; i < _nodes.size(); i++){
      NodeWrapper n = (NodeWrapper)_nodes.get(i);
      if(n.getName().equals(name)){
        return n;
      }
    }
    return null;
  }
  
  static class NodeWrapper{
    Node _node;
    String _alias;
    NodeWrapper(String alias, Node n){
      _node = n;
      if(alias == null)
        _alias = n.getName();
      else
        _alias = alias;
    }
    
    String getName(){
      return _alias;    
    }
    
    Node node(){
      return _node;
    }
  }

}
