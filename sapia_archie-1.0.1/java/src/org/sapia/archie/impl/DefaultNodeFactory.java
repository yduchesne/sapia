package org.sapia.archie.impl;

import org.sapia.archie.Node;
import org.sapia.archie.NodeFactory;
import org.sapia.archie.ProcessingException;


/**
 * A <code>NodeFactory</code> that creates <code>DefaultNode</code>s.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class DefaultNodeFactory implements NodeFactory {
  
  /**
   * @see NodeFactory#newNode()
   */
  public Node newNode() throws ProcessingException{
    return new DefaultNode();
  }
}
