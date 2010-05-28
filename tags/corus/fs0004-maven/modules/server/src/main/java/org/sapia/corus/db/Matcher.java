package org.sapia.corus.db;


/**
 * This interface specifies matching behavior.
 * 
 * @author yduchesne
 *
 * @param <V>
 */
public interface Matcher<V> {
  
  /**
   * @param toMatch the object for which to determine if there is a match.
   * 
   * @return <code>true</code> if this matcher determines that the given
   * object matches its internal criteria.
   */
  public boolean matches(V toMatch);
}
