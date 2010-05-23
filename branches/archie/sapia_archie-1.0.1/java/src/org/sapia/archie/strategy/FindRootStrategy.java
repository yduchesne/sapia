package org.sapia.archie.strategy;

import org.sapia.archie.Name;
import org.sapia.archie.Node;
import org.sapia.archie.NotFoundException;
import org.sapia.archie.ProcessingException;


/**
 * A lookup strategy that is meant to return the root <code>Node</code>
 * of a given node hierarchy.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class FindRootStrategy implements LookupStrategy {
  
  /**
   * Returns the root <code>Node</code> of the hierarchy of which the
   * given "from" <code>Node</code> is part.
   * 
   * @see LookupStrategy#lookup(Name, Node)
   */
  public Object lookup(Name n, Node from)
                throws NotFoundException, ProcessingException {
    Node current = from;

    while (current.getParent() != null) {
      current = current.getParent();
    }

    return current;
  }
}
