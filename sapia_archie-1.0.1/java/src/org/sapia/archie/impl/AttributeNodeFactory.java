package org.sapia.archie.impl;

import java.util.HashMap;

import org.sapia.archie.Node;
import org.sapia.archie.NodeFactory;
import org.sapia.archie.ProcessingException;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class AttributeNodeFactory implements NodeFactory{
  
  /**
   * @see org.sapia.archie.NodeFactory#newNode()
   */
  public Node newNode() throws ProcessingException {
    return new AttributeNode(new HashMap(), new HashMap(), this);
  }
  
  
}
