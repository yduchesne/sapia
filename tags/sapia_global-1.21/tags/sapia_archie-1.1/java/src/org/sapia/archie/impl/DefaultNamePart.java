package org.sapia.archie.impl;

import org.sapia.archie.NamePart;


/**
 * Default <code>NamePart</code> implementation.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class DefaultNamePart implements NamePart, Comparable{
  private String _part;

  public DefaultNamePart(String part) {
    _part = part;
  }
  
  /**
   * @see NamePart#asString()
   */
  public String asString() {
    return _part;
  }

  public int hashCode() {
    return _part.hashCode();
  }

  public boolean equals(Object o) {
    try {
      return ((NamePart) o).asString().equals(_part);
    } catch (ClassCastException e) {
      return false;
    }
  }

  public int compareTo(Object o) {
    return _part.compareTo(((NamePart) o).asString());
  }

  public String toString() {
    return _part;
  }
}
