package org.sapia.archie.impl;

import java.util.HashMap;

import org.sapia.archie.Node;
import org.sapia.archie.NodeFactory;
import org.sapia.archie.ProcessingException;

/**
 * @author Yanick Duchesne
 */
public class AttributeNodeFactory implements NodeFactory{
  
  /**
   * @see org.sapia.archie.NodeFactory#newNode()
   */
  public Node newNode() throws ProcessingException {
    return new AttributeNode(new HashMap(), new HashMap(), this);
  }
  
  
}
