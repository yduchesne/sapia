package org.sapia.regis.remote;

import java.rmi.Remote;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.Property;
import org.sapia.regis.Query;

/**
 * An instance of this class wraps a <code>Node</code> to make it
 * remotable.
 *  
 * @author yduchesne
 *
 */
public class RemoteNode implements Node, Remote{
  
  private Node delegate;
  
  RemoteNode(Node delegate){
    this.delegate = delegate;
  }

  public Path getAbsolutePath() {
    return node().getAbsolutePath();
  }

  public Node getChild(Path path) {
    return wrap(node().getChild(path));
  }

  public Node getChild(String name) {
    return wrap(node().getChild(name));
  }

  public Collection getChildren() {
    return wrap(node().getChildren());
  }

  public Collection getLinks(boolean prepended) {
    return wrap(node().getLinks(prepended));
  }
  
  public Collection getIncludes() {
    return wrap(node().getIncludes());
  }
  
  public Collection getNodes(Query query) {
    return wrap(node().getNodes(query));
  }

  public String getName() {
    return node().getName();
  }
  
  public Collection getChildrenNames() {
    return node().getChildrenNames();
  }

  public Node getParent() {
    return wrap(node().getParent());
  }

  public Map getProperties() {
    return node().getProperties();
  }
 
  public Map getProperties(Map values) {
    return node().getProperties(values);
  }

  public Property getProperty(String key) {
    return node().getProperty(key);
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
    return node().isRoot();
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
  
  private Collection wrap(Collection nodes){
    Collection toReturn = new ArrayList(nodes.size());
    Iterator itr = nodes.iterator();
    while(itr.hasNext()){
      Node n = (Node)itr.next();
      toReturn.add(new RemoteNode(n));
    }
    return toReturn;
  }
  
  private Node wrap(Node node){
    if (node == null) {
      return null;
    } else {
      return new RemoteNode(node);
    }
  }
  
  Node node(){
    return RemoteSessions.get().attach(delegate);
  }
}
