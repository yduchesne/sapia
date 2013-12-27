package org.sapia.archie.strategy;

import org.sapia.archie.Name;
import org.sapia.archie.NamePart;
import org.sapia.archie.Node;
import org.sapia.archie.NotFoundException;
import org.sapia.archie.ProcessingException;


/**
 * Looks up a value, given a name. Searches from a given node, following
 * the path evaluated from that node.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class DefaultLookupStrategy extends DefaultLookupNodeStrategy {
  public DefaultLookupStrategy() {
    super(false);
  }

  /**
   * @see LookupStrategy#lookup(Name, Node)
   */
  public Object lookup(Name n, Node from)
                throws NotFoundException, ProcessingException {
    NamePart last = n.chopLast();
    Node     node = (Node) super.lookup(n, from);

    Object   value = node.getValue(last);

    if (value == null) {
      throw new NotFoundException(n.toString());
    }

    return value;
  }
}
