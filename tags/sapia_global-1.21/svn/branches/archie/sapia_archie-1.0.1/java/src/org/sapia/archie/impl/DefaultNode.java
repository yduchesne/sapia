package org.sapia.archie.impl;

import java.util.HashMap;

import org.sapia.archie.ProcessingException;


/**
 * Default <code>Node</code> implementation.
 * 
 * @see org.sapia.archie.impl.SingleValueNode 
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class DefaultNode extends SingleValueNode {
  
  public DefaultNode() throws ProcessingException {
    super(new HashMap(), new HashMap(), new DefaultNodeFactory());
  }
}
