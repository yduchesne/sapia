package org.sapia.magnet.domain;

import java.util.Collection;
import java.util.TreeSet;

import org.sapia.magnet.render.RenderingException;


/**
 *
 *
 * @author Jean-Cedric Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html" target="sapia-license">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class HttpProtocolHandler implements ProtocolHandlerIF {

  /**
   * Handle the rendering of the path object for a specific protocol by
   * resolving the resources of the path.
   *
   * @param aPath The path object to render.
   * @param aSortingOrder The sorting order of the collection to return.
   * @return The collection of <CODE>Resource</CODE> objects.
   * @exception RenderingException If an error occurs while resolving the path.
   */
  public Collection<Resource> resolveResources(Path aPath, SortingOrder aSortingOrder) throws RenderingException {
    // Validate the arguments
    if (aPath == null) {
      throw new IllegalArgumentException("The path object passed in null");
    } else if (!aPath.getProtocol().equals(Path.PROTOCOL_HTTP)) {
      throw new IllegalArgumentException("The protocol of the path is not 'http' but " + aPath.getProtocol());
    } else if (aPath.getDirectory() == null) {
      throw new IllegalArgumentException("The directory of the path passed in is null");
    }

    // Create the resources for the included files
    TreeSet<Resource> someResources;
    if (SortingOrder.ASCENDING == aSortingOrder) {
      someResources = new TreeSet<Resource>(new Resource.AscendingComparator());
    } else if (SortingOrder.DESCENDING == aSortingOrder) {
      someResources = new TreeSet<Resource>(new Resource.DescendingComparator());
    } else {
      someResources = new TreeSet<Resource>();
    }
    int anIndex = 0;

    for (Include incl: aPath.getIncludes()) {
      StringBuffer anURL = new StringBuffer();
      anURL.append(Path.PROTOCOL_HTTP).append("://").
            append(aPath.getHost()).
            append(aPath.getDirectory()).append("/").
            append(incl.getPattern());
      someResources.add(new Resource(anURL.toString(), anIndex++));
    }

    return someResources;
  }
  
}
