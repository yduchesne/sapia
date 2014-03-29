package org.sapia.archie.strategy;

import org.sapia.archie.Name;
import org.sapia.archie.Node;
import org.sapia.archie.NotFoundException;
import org.sapia.archie.ProcessingException;


/**
 * This interface specifies lookup strategy behavior. It decouples lookup
 * logic from node implementations. Different strategies can be implemented,
 * according to applications needs, without having to mingle with node
 * implementations. 
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface LookupStrategy {
  
  /**
   * Performs the lookup operation from the given node.
   * 
   * @param n the <code>Name</code> of the value to lookup.
   * @param from the <code>Node</code> from which the search starts.
   * @return the <code>Object</code> that corresponds to the given name.
   * 
   * @throws NotFoundException if no object is found for the given name.
   * @throws ProcessingException if a problem occurs while performing the lookup. 
   */
  public Object lookup(Name n, Node from)
                throws NotFoundException, ProcessingException;
}
