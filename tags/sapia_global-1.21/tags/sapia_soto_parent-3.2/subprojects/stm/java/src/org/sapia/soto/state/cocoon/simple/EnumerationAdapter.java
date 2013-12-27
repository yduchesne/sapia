package org.sapia.soto.state.cocoon.simple;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class EnumerationAdapter implements Enumeration {

  private Iterator _itr;

  public EnumerationAdapter(Iterator itr) {
    _itr = itr;
  }

  /**
   * @see java.util.Enumeration#hasMoreElements()
   */
  public boolean hasMoreElements() {
    return _itr.hasNext();
  }

  /**
   * @see java.util.Enumeration#nextElement()
   */
  public Object nextElement() {
    return _itr.next();
  }

}
