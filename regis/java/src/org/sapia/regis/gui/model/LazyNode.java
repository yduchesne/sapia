package org.sapia.regis.gui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.Property;
import org.sapia.regis.Query;
import org.sapia.regis.RegisLog;

public class LazyNode implements Node{
  
  private Node _delegate;
  private Node _parent;
  private String _name;
  private Path _path;
  
  public LazyNode(Node delegate){
    _delegate = delegate;
    _name = _delegate.getName();
    _delegate = delegate;
    _parent = (Node)delegate.getParent();
    _path = delegate.getAbsolutePath();
  }
  
  public LazyNode(Node parent, String name){
    _name = name;
    _parent = parent;
    Path path = _parent.getAbsolutePath();
    if(path == null){
      _path = Path.parse(_name);      
    }
    else{
      _path = Path.parse(path.toString()+"/"+_name);
    }
  }

  public Path getAbsolutePath() {
    return _path;
  }
  
  public Node getChild(Path path) {
    return node().getChild(path);
  }
  
  public Node getChild(String name) {
    return node().getChild(name);
  }
  
  public Collection getChildren() {
    Iterator itr = node().getChildrenNames().iterator();
    List children = new ArrayList();
    while(itr.hasNext()){
      String name = (String)itr.next();
      LazyNode lazy = new LazyNode(this, name);
      children.add(lazy);
    }
    return children;
  }
  
  public Collection getChildrenNames() {
    return node().getChildrenNames();
  }
  
  public Collection getIncludes() {
    return node().getIncludes();
  }
  
  public Collection getLinks(boolean prepended) {
    return node().getLinks(prepended);
  }
  
  public String getName() {
    return _name;
  }
  
  public Collection getNodes(Query query) {
    return node().getNodes(query);
  }
  
  public Node getParent() {
    return _parent;
  }
  
  public Map getProperties() {
    
    Map props = node().getProperties();
    RegisLog.debug(getClass(), ""+props);
    return props;
    
  }
  
  public Property getProperty(String key) {
    return node().getProperty(key);
  }
  
  public Map getProperties(Map values) {
    return node().getProperties(values);
  }
  
  public Collection getPropertyKeys() {
    return node().getPropertyKeys();
  }
  
  public String getType() {
    return node().getType();
  }
  
  public boolean isInheritsParent() {
    return node().isInheritsParent();
  }
  
  public boolean isRoot() {
    return _parent == null;
  }
  
  public long lastModifChecksum() {
    return node().lastModifChecksum();
  }
  
  public Property renderProperty(String key) {
    return node().renderProperty(key);
  }
  
  public Property renderProperty(String key, Map values) {
    return node().renderProperty(key, values);
  }
  
  synchronized Node node(){
    if(_delegate == null){
      _delegate = _parent.getChild(_name);
      if(_delegate == null){
        throw new IllegalStateException("No child node found for: " + _name);
      }
    }
    return _delegate;
  }
  
  

}
