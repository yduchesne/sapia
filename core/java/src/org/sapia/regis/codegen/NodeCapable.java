package org.sapia.regis.codegen;

import org.sapia.regis.Node;

/**
 * Interface that specifies single {@link #getNode()} method.
 * 
 * @author yduchesne
 *
 */
public interface NodeCapable {
  
  /**
   * @return the {@link Node} that this instance encapsulates.
   */
  public Node getNode();

  public <T extends NodeCapable> T getInstanceFor(Class<T> nodeClass);

}
