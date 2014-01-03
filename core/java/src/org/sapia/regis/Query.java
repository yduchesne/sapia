package org.sapia.regis;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * An instance of this class is used to acquire a given collection of nodes,
 * under a given node instance.
 * <p>
 * In other words, it is meant to filter the <code>Node</code>s that a
 * given node holds, based on the name/value of the filtered nodes.
 * <p>
 * For example, imagine that we have a node holding child nodes that 
 * correspond to user accounts (username/password). The username and password
 * would be kept as properties, in each account node.
 * <p>
 * To acquire the node corresponding to a given account, we could query
 * the node holding each the account nodes:
 * <pre>
 *   Collection result = accounts.getNodes(Query.create().addCrit("username", "jsmith"));
 *   if(result.size() == 0) {
 *     throw new IllegalArgumentException("Invalid username");
 *   }
 *   else if(result.size() > 1){
 *     throw new IllegalStateException("System corrupted; more than one account with same username");
 *   }
 *   else{
 *      Node account = (Node)result.iterator().next();
 *      if(account.getProperty("passwd").asString().equals(givenPasswd)){
 *        ...
 *      }
 *      else{
 *        throw new AuthenticationException("Invalid login; wrong username and/or password");
 *      }
 *   }
 * </pre>
 * <p>
 * Now in the example above, the queried node is the one that is expected to contain the
 * nodes that are to be filtered. But imagine that the parent node is under the "system/accounts" 
 * path (under the root). Using the root node of the registry, we would retrieve our desired
 * account as follows:
 * <pre>
 *   Collection result = root.getNodes(Query.create("system/accounts")
 *     .addCrit("username", "jsmith"));
 * </pre>
 * 
 * @see org.sapia.regis.Node#getNodes(Query)
 * 
 * @author yduchesne
 *
 */
public class Query implements Serializable{

  static final long serialVersionUID = 1L;
  
  private Path _path;
  private Map _criteria;
  
  /**
   * @param path a <code>Path</code> corresponding to a <code>Node</code> whose
   * children nodes should be returned, provided they match this instance's criteria.
   * @return
   */
  public Query setPath(Path path){
    _path = path;
    return this;
  }
  
  /**
   * @see #setPath(Path)
   * @see Path#parse(String)
   */
  public Query setPath(String path){
    _path = Path.parse(path);
    return this;
  }  
  
  /**
   * @return this instance's <code>Path</code>, or null if this instance 
   * has no path.
   */
  public Path getPath(){
    return _path;
  }
  
  /**
   * Adds a criterion, corresponding to the expected name/value of a property. This
   * method can be called in chained invocations:
   * <pre>
   * query.addCrit("prop1", "value1").addCrit("prop2", "value2");
   * </pre>
   * 
   * @param name a property name.
   * @param value a property value.
   * @return this instance.
   */
  public Query addCrit(String name, String value){
    crit().put(name, value);
    return this;
  }
  
  /**
   * @return the <code>Map</code> of criteria held by this instance.
   */
  public Map getCriteria(){
    return _criteria == null ? Collections.EMPTY_MAP : _criteria;
  }
  
  /**
   * @return a new instance of this class.
   */
  public static Query create(){
    return new Query();
  }
  
  /**
   * @see #create(Path)
   */
  public static Query create(String path){
    return create(Path.parse(path));
  }  
  
  /**
   * @param path a <code>Path</code>.
   * @return anew instance of this class, bound to the given path.
   */
  public static Query create(Path path){
    Query q = new Query();
    q.setPath(path);
    return q;
  }
  
  private Map crit(){
    if(_criteria == null) _criteria = new HashMap();
    return _criteria;
  }
}
