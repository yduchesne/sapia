package org.sapia.corus.db;

import java.util.Collection;
import java.util.Iterator;


/**
 * Specifies behavior similar to the <code>java.util.Map</code>
 * interface. Instances of this interface are expected to provide
 * persistency.
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface DbMap<K, V> {
  /**
   * Puts the an object in this map, mapped to the given key.
   *
   * @param key the key of the passed in value.
   * @param value the value to persist.
   */
  public void put(K key, V value);

  /**
   * Removes the object that corresponds to the given key.
   *
   * @param key the key for which the corresponding object should be
   * removed.
   */
  public void remove(K key);

  /**
   * Returns an iterator of this instance's values.
   *
   * @return an <code>Iterator</code>.
   */
  public Iterator<V> values();

  /**
   * Returns an iterator of this instance's keys.
   *
   * @return an <code>Iterator</code>.
   */
  public Iterator<K> keys();

  /**
   * Closes this instance - releases all resources held by it.
   */
  public void close();

  /**
   * Return the object for the given key.
   *
   * @param key a key for which to return the corresponding object.
   * @return returns the object corresponding to the passed in key,
   * or <code>null</code> if no object could be found.
   */
  public V get(K key);
  
  /**
   * Clears this instance's values.
   */
  public void clear();
  
  /**
   * @param template the object that serves as a template for matching.
   * @return the {@link Matcher} corresponding to the given template object.
   */
  public Matcher<V> createMatcherFor(V template);

  /**
   * @param matcher a matcher to use for selection
   * @return an {@link Iterator} "containing" objects that are determined
   * to 'match' by the given matcher.
   */
  public Collection<V> values(Matcher<V> matcher);
}
