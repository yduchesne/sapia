package org.sapia.archie;


/**
 * Corresponds to the the Visitor pattern; allows implementations to traverse
 * {@link Node} hierarchies.
 * 
 * @author Yanick Duchesne
 */
public interface NodeVisitor {
  
  /**
   * 
   * @param node a {@link Node} to visit.
   * @return <code>false</code> if traversal should be aborted, <code>true</code>
   * otherwise.
   * @see Node#accept(NodeVisitor)
   */
  public boolean visit(Node node);
}
