package org.sapia.corus.util;

/**
 * This interface specifies matching behavior.
 * @author yduchesne
 *
 * @param <T>
 */
public interface Matcher<T>{
  
  /**
   * @param object
   * @return true of the given object matches this instance's criteria.
   */
  public boolean matches(T object);
  
}
