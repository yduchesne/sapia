package org.sapia.archie.impl;

import org.sapia.archie.NamePart;


/**
 * Default {@link NamePart} implementation.
 * 
 * @author Yanick Duchesne
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
