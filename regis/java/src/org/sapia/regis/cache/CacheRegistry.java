package org.sapia.regis.cache;

import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.RegisSession;
import org.sapia.regis.Registry;

/**
 * This class implements a read-only <code>Registry</code> that is meant to wrap
 * another registry, whose state will be cached by this instance.
 * <p>
 * Cache refresh occurs synchronously, at a given time interval.
 * 
 * @see org.sapia.regis.cache.CacheNode
 * 
 * @author yduchesne
 *
 */
public class CacheRegistry implements Registry{
  
  private Registry _internal;
  private CacheNode _root;
  
  /**
   * @param reg a <code>Registry</code> whose state should be cached.
   * @param refreshIntervalMillis an refresh interval, in milliseconds.
   * @param render if <code>true</code>, will perform property interpolation within the nodes.
   */
  public CacheRegistry(Registry reg, long refreshIntervalMillis, boolean render){
    _root = new CacheNode(Path.parse(Node.ROOT_NAME), reg, refreshIntervalMillis, render);
    _internal = reg;
  }

  public RegisSession open() {
    CacheRegisSession session = new CacheRegisSession(_internal.open());
    CacheSessions.join(session);
    return session;
  }
  
  public Node getRoot() {
    return _root;
  }
  
  /**
   * @return the <code>Registry</code> wrapped by this instance.
   */
  public Registry internal(){
    return _internal;
  }
  
  public void close() {
    _internal.close();
  }
}
