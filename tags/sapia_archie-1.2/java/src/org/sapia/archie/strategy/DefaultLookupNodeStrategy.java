package org.sapia.archie.strategy;

import org.sapia.archie.Name;
import org.sapia.archie.NamePart;
import org.sapia.archie.Node;
import org.sapia.archie.NotFoundException;
import org.sapia.archie.ProcessingException;


/**
 * Looks up a <code>Node</code> corresponding to a given name.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class DefaultLookupNodeStrategy implements LookupStrategy {
  private boolean _create;

  /**
   * @param createNotExistingNodes if <code>true</code>, parts of looked up names 
   * that have no corresponding node will have a node created for them.
   */
  public DefaultLookupNodeStrategy(boolean createNotExistingNodes) {
    _create = createNotExistingNodes;
  }

  public Object lookup(Name n, Node from)
                throws NotFoundException, ProcessingException {
    NamePart currentPart;
    
    if(n.count() == 0){
      return from;
    }
    else if (n.first().asString().length() == 0) {
      n.chopFirst();
      from = (Node) new FindRootStrategy().lookup(n, from);
    }

    Node currentNode = from;

    while (n.hasNextPart()) {
      currentPart = n.nextPart();

      if (currentNode.getChild(currentPart) == null) {
        if (!_create) {
          throw new NotFoundException("Resolved: " +
                                      n.getTo(n.getCurrentIndex()) +
                                      "; remaining: " +
                                      n.getFrom(n.getCurrentIndex()));
        } else {
          currentNode = currentNode.createChild(currentPart);
        }
      } else {
        currentNode = currentNode.getChild(currentPart);
      }
    }

    return currentNode;
  }
}
