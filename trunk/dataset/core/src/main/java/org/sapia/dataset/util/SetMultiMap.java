package org.sapia.dataset.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.sapia.dataset.func.NoArgFunction;

/**
 * A {@link Set}-based {@link MultiMap} implementation.
 * 
 * @author yduchesne
 */
public class SetMultiMap<K, V> implements MultiMap<K, V>{

  private NoArgFunction<Set<V>>     setCreator;
  private Map<K, Set<V>> delegate = new HashMap<>();

  /**
   * @param setCreator a {@link NoArgFunction} which is used to create the {@link Set}
   * instances bound to the keys in the map.
   */
  public SetMultiMap(NoArgFunction<Set<V>> setCreator) {
    this.setCreator = setCreator;
  }
  
  @Override
  public Collection<V> get(K key) {
    Set<V> values = delegate.get(key);
    if (values == null) {
      values = Collections.emptySet();
    }
    return values;
  }
  
  @Override
  public Set<K> keySet() {
    return delegate.keySet();
  }
  
  @Override
  public MultiMap<K, V> put(K key, V value) {
    Set<V> values = delegate.get(key);
    if (values == null) {
      values = setCreator.call();
    }
    values.add(value);
    delegate.put(key, values);
    return this;
  }
  
  // --------------------------------------------------------------------------
  // Factory methods
  
  /**
   * @return a new {@link HashSet}-based {@link SetMultiMap}.
   */
  public static <K, V> SetMultiMap<K, V> createHashSetMultiMap() {
    return new SetMultiMap<>(new NoArgFunction<Set<V>>() {
      @Override
      public Set<V> call() {
        return new HashSet<>();
      }
    });
  }
  
  /**
   * @return a new {@link TreeSet}-based {@link SetMultiMap}.
   */
  public static <K, V> SetMultiMap<K, V> createTreeSetMultiMap() {
    return new SetMultiMap<>(new NoArgFunction<Set<V>>() {
      @Override
      public Set<V> call() {
        return new TreeSet<>();
      }
    });
  }
  
  
}
