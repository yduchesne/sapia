package org.sapia.soto;

import org.sapia.util.CompositeException;

/**
 * Thrown when an object is not found.
 * 
 * @see org.sapia.soto.SotoContainer#lookup(String)
 * 
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2003 <a
 *         href="http://www.sapia-oss.org">Sapia Open Source Software </a>. All
 *         Rights Reserved.</dd>
 *         </dt>
 *         <dt><b>License: </b>
 *         <dd>Read the license.txt file of the jar or visit the <a
 *         href="http://www.sapia-oss.org/license.html">license page </a> at the
 *         Sapia OSS web site</dd>
 *         </dt>
 *         </dl>
 */
public class NotFoundException extends CompositeException {
  /**
   * Constructor for ServiceNotFoundException.
   * 
   * @param arg0
   * @param arg1
   */
  public NotFoundException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  /**
   * Constructor for ServiceNotFoundException.
   * 
   * @param arg0
   */
  public NotFoundException(String arg0) {
    super(arg0);
  }
}
