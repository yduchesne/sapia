package org.sapia.dataset.util;

import java.util.Collection;
import java.util.Set;

/**
 * Binds a single key to a collection of values.
 * 
 * @author yduchesne
 */
public interface MultiMap<K, V> {

  /**
   * @return the {@link Set} of keys held by this instance.
   */
  public Set<K> keySet();
  
  /**
   * @param key a key.
   * @return the {@link Collection} of values bound to the given key - will be empty rather than
   * null if no element has yet been bound to the key.
   */
  public Collection<V> get(K key);
 
  /**
   * Adds the given key/value to this map.
   * 
   * @param key a key.
   * @param value a value.
   * @return this instance, for chained-invocation support.
   */
  public MultiMap<K, V> put(K key, V value);
}
