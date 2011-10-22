package org.sapia.regis;

/**
 * This interface models a "configuration registry", in which configuration is 
 * hierarchically kept.
 * <p>
 * All accesses to a registry, in the context of a given thread, must be done using
 * a session that was acquired from that registry, according to the following programming
 * model:
 * <pre>
 * RegisSession session = registry.open();
 * try{
 *   Node root = registry.getRoot();
 *   ...
 * }finally{
 *   session.close();
 * }
 * </pre>
 * 
 * @author yduchesne
 *
 */
public interface Registry{

  /**
   * @return the root <code>Node</code> of this instance.
   */
  public Node getRoot();
  
  /**
   * Opens a session linked to this instance and returns it.
   * 
   * @return a <code>RegisSession</code>.
   */
  public RegisSession open();
  
  /**
   * Closes this instance.
   */
  public void close();
}
