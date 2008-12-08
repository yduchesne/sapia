package org.sapia.regis.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sapia.regis.DuplicateNodeException;
import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.Property;
import org.sapia.regis.Query;
import org.sapia.regis.RegisDebug;
import org.sapia.regis.Registry;
import org.sapia.regis.impl.PropertyImpl;
import org.sapia.regis.util.Utils;
import org.sapia.util.text.MapContext;
import org.sapia.util.text.SystemContext;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.text.TemplateException;
import org.sapia.util.text.TemplateFactory;

/**
 * An instance of this class wraps a given <code>Node</code>, copying the
 * properties of the wrapped node and refreshing them on a periodic basis, in a 
 * synchronous manner, and at a predefined time interval.
 * <p>
 * All child nodes of an instance of this class also perform caching behaviour.
 * <p>
 * Note that this instance is read-only. All "write" methods throw 
 * <code>UnsupportedOperationException</code>s.
 * 
 * @author yduchesne
 *
 */
public class CacheNode implements Node{
  
  private Map props;
  private Path path;
  private String name, type;
  private Registry registry;
  private Collection childrenNames;
  private long lastChecksum, lastCheck, interval;
  private Node ref;
  
  CacheNode(
      Node node, 
      Registry registry,
      long refreshIntervalMillis){
    init(node, registry, refreshIntervalMillis);
  }  
  
  CacheNode(
      Path path, 
      Registry registry,
      long refreshIntervalMillis){
    init(registry.getRoot().getChild(path), registry, refreshIntervalMillis);
  }
  
  void init(Node node, 
      Registry registry,
      long refreshIntervalMillis){
    this.name = node.getName();
    this.type = node.getType();
    this.path = node.getAbsolutePath();
    this.props = Collections.synchronizedMap(node.getProperties());
    this.registry = registry;
    this.interval = refreshIntervalMillis;
    this.childrenNames = node.getChildrenNames();
    this.lastChecksum = node.lastModifChecksum();
    this.lastCheck = System.currentTimeMillis();
    this.ref = node;
  }

  public Collection getPropertyKeys() {
    refresh();
    return new ArrayList(props().keySet());
  }

  public Path getAbsolutePath() {
    return path;
  }

  public Node getChild(Path path) {
    Node child = node().getChild(path);
    if(child == null){
      return child;
    }
    else return new CacheNode(child, registry, interval);
  }

  public Node getChild(String name) {
    Node child = node().getChild(name);
    if(child == null){
      return child;
    }
    else return new CacheNode(child, registry, interval);
  }

  public Collection getChildren() {
    return doGetNodes(node().getChildren());
  }
  
  public Collection getChildrenNames() {
    return this.childrenNames;
  }
  
  public Collection getIncludes() {
    return doGetNodes(node().getIncludes());
  }

  public Collection getLinks(boolean prepended) {
    return doGetNodes(node().getLinks(prepended));
  }
  
  public Collection getNodes(Query query) {
    return doGetNodes(node().getNodes(query));
  }

  public String getName() {
    return name;
  }
  
  public String getType() {
    return type;
  }

  public Node getParent() {
    return new CacheNode(node().getParent(), registry, interval);
  }

  public Map getProperties() {
    return new HashMap(props());
  }

  public Map getProperties(Map values) {
     Map toReturn = props();
     toReturn = Utils.replaceVars(new MapContext(values, new SystemContext(), false), toReturn);
     return node().getProperties(new RemoteMapProxy(values));
  }  

  public Property getProperty(String key) {
    refresh();
    String value = (String)props().get(key);
    if(value == null){
      return new PropertyImpl(key, value);
    }
    TemplateFactory fac = new TemplateFactory();
    try {
      TemplateContextIF ctx = new SystemContext();
      value = fac.parse(value).render(ctx);
      return new CacheProperty(key, value, this);
    }catch(TemplateException e){
      throw new RuntimeException("Could not render property " + key + " from value '" + value + "' (current node is "+ getAbsolutePath() + ")", e);
    }    
  }
  
  public Property renderProperty(String key) {
    return getProperty(key);
  }
  
  public Property renderProperty(String key, Map values) {
    refresh();
    String value = (String)props().get(key);
    if(value == null){
      return new PropertyImpl(key, value);
    } else {
      value = doRenderValue(key, value, values);
      if (value != null && value.contains(TemplateFactory.DEFAULT_STARTING_DELIMITER)) {
        String previousValue = null;
        while (!value.equals(previousValue)) {
          previousValue = value;
          value = doRenderValue(key, value, values);
        }
      }
      
      if (value == null || value.contains(TemplateFactory.DEFAULT_STARTING_DELIMITER)) {
        throw new RuntimeException("Could not render property " + key + " from value '" + value + "' (current node is "+ getAbsolutePath() + ")");
      } else {
        return new PropertyImpl(key, value);
      }
    }
  }
  
  protected String doRenderValue(String aKey, String aValue, Map someProperties) {
    TemplateFactory fac = new TemplateFactory();
    try {
      MapContext context = new MapContext(props(), new SystemContext(), false);
      String newValue = fac.parse(aValue).render(new MapContext(someProperties, context, false));
      return newValue;
    }catch(TemplateException te) {
      throw new RuntimeException("Could not render property " + aKey + " from value '" + aValue + "' (current node is "+ getAbsolutePath() + ")", te);
    }
  }  

  public boolean isInheritsParent() {
    return node().isInheritsParent();
  }

  public boolean isRoot() {
    return name == null || name.equals(Node.ROOT_NAME);
  }

  public long lastModifChecksum() {
    refresh();
    return lastChecksum;
  }
  
  /**
   * @return the <code>RegistryNode</code> corresponding to this instance
   */
  public Node internal(){
    return node();
  }
  
  protected Node node(){
    Node node = registry.getRoot().getChild(path);
    if(node == null){
      throw new NoSuchNodeException(path.toString());
    }
    ref = node;    
    return node;
  }
  
  private synchronized boolean refresh(){
    if(System.currentTimeMillis() - lastCheck > interval){
      synchronized(this){
        if(System.currentTimeMillis() - lastCheck > interval){
          if(RegisDebug.enabled){
            RegisDebug.debug(this, "refreshing...");
          }
          lastCheck = System.currentTimeMillis();          
          Node node = node();
          long currentChecksum = node.lastModifChecksum();
          if(lastChecksum != currentChecksum){
            try{
              Map newProps = node.getProperties();
              props = newProps;
              childrenNames = node.getChildrenNames();
              lastChecksum = currentChecksum;
            }catch(RuntimeException e){}
          }
        }
      }
      return true;
    }
    return false;
  }
  
  private Map props(){
    Map toReturn = props;
    return toReturn;
  }
  
  private List doGetNodes(Collection original){
    Iterator nodes = original.iterator();
    List toReturn = new ArrayList(original.size());
    while(nodes.hasNext()){
      Node node = (Node)nodes.next();
      toReturn.add(new CacheNode(node, registry, interval));
    }
    return toReturn;
  }
  
  ////////////////////// UNSUPPORTED /////////////////////
  
  /**
   * @throws UnsupportedOperationException
   */  
  public void deleteChild(String name) {
    throw new UnsupportedOperationException();    
  }

  /**
   * @throws UnsupportedOperationException
   */  
  public void deleteChildren() {
    throw new UnsupportedOperationException();
  }
  
  /**
   * @throws UnsupportedOperationException
   */  
  public void deleteProperties() {
    throw new UnsupportedOperationException();    
  }

  /**
   * @throws UnsupportedOperationException
   */  
  public void deleteProperty(String key) {
    throw new UnsupportedOperationException();    
  }
  
  /**
   * @throws UnsupportedOperationException
   */
  public void appendLink(Node node) {
    throw new UnsupportedOperationException();    
  }

  /**
   * @throws UnsupportedOperationException
   */  
  public Node createChild(String name) throws DuplicateNodeException {
    throw new UnsupportedOperationException();
  }
  
  
  /**
   * @throws UnsupportedOperationException
   */  
  public void moveTo(Node newParent) {
    throw new UnsupportedOperationException();    
  }

  /**
   * @throws UnsupportedOperationException
   */    
  public void prependLink(Node node) {
    throw new UnsupportedOperationException();    
  }

  /**
   * @throws UnsupportedOperationException
   */    
  public void removeAppendedLink(Node node) {
    throw new UnsupportedOperationException();    
  }

  /**
   * @throws UnsupportedOperationException
   */    
  public void removePrependedLink(Node node) {
    throw new UnsupportedOperationException();    
  }

  /**
   * @throws UnsupportedOperationException
   */    
  public void setInheritsParent(boolean inheritsParent) {
    throw new UnsupportedOperationException();    
  }

  /**
   * @throws UnsupportedOperationException
   */    
  public void setProperty(String key, String value) {
    throw new UnsupportedOperationException();    
  }
  
  /**
   * @throws UnsupportedOperationException
   */    
  public void setType(String type) {
    throw new UnsupportedOperationException();    
  }    
}
