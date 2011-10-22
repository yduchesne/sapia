package org.sapia.regis;

/**
 * An instance of this class must be acquired from a <code>Registry</code> when
 * performing operations on that registry, or on any one of its nodes.
 * <p>
 * An instance of this class is not thread-safe. It must be used in the context
 * of a single thread, and it must be closed once the thread as completed a unit
 * of work.
 * <p>
 * <i>Working with an instance of this class is similar to working with an Hibernate
 * session.</i>
 *  
 * @author yduchesne
 *
 */
public interface RegisSession {
  
  /**
   * This method closes this instance (destroying any system resources
   * and releasing any objects that this instance may hold)
   */
  public void close();
  
  /**
   * @param node a <code>Node</code>
   * @return a <code>Node</code>, corresponding to the given node, but
   * "attached" to this session.
   */
  public Node attach(Node node);

}
