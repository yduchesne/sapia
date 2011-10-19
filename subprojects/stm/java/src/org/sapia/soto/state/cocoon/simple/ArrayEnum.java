package org.sapia.soto.state.cocoon.simple;

import java.util.Enumeration;

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
public class ArrayEnum implements Enumeration {

  private Object[] _arr;
  private int      _index;

  public ArrayEnum(Object[] array) {
    _arr = array;
  }

  /**
   * @see java.util.Enumeration#hasMoreElements()
   */
  public boolean hasMoreElements() {
    return _index < _arr.length;
  }

  /**
   * @see java.util.Enumeration#nextElement()
   */
  public Object nextElement() {
    return _arr[_index++];
  }

}
