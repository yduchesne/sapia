package org.sapia.soto.state.cocoon.simple;

import java.util.Enumeration;
import java.util.NoSuchElementException;

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
public class EmptyEnum implements Enumeration {

  /**
   * @see java.util.Enumeration#hasMoreElements()
   */
  public boolean hasMoreElements() {
    return false;
  }

  /**
   * @see java.util.Enumeration#nextElement()
   */
  public Object nextElement() {
    throw new NoSuchElementException();
  }

}
