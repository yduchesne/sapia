package org.sapia.archie.impl;

import org.sapia.archie.Node;
import org.sapia.archie.NodeFactory;
import org.sapia.archie.ProcessingException;


/**
 * A {@link NodeFactory} that creates {@link DefaultNode}s.
 * 
 * @author Yanick Duchesne
 */
public class DefaultNodeFactory implements NodeFactory {
  
  /**
   * @see NodeFactory#newNode()
   */
  public Node newNode() throws ProcessingException{
    return new DefaultNode();
  }
}
