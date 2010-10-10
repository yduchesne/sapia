package org.sapia.regis.traversal;

import org.sapia.regis.Node;

/**
 * This interface is designed according to the Visitor pattern.
 * @author yduchesne
 *
 */
public interface Visitor {

  
  public void visit(Node node);
}
