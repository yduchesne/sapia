package org.sapia.archie.impl;

import java.util.HashMap;

import org.sapia.archie.NamePart;
import org.sapia.archie.Node;
import org.sapia.archie.ProcessingException;


/**
 * Default {@link Node} implementation.
 * 
 * @see org.sapia.archie.impl.SingleValueNode 
 * 
 * @author Yanick Duchesne
 */
public class DefaultNode extends SingleValueNode {
  
  public DefaultNode() throws ProcessingException {
    super(new HashMap<NamePart, Node>(), new HashMap<NamePart, Object>(), new DefaultNodeFactory());
  }
}
