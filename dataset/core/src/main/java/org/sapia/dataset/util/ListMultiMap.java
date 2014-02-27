package org.sapia.dataset.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sapia.dataset.func.NoArgFunction;

/**
 * A {@link List}-based {@link MultiMap} implementation.
 * 
 * @author yduchesne
 * 
 */
public class ListMultiMap<K, V> implements MultiMap<K, V>{

  private NoArgFunction<List<V>> listCreator;
  private Map<K, List<V>> delegate = new HashMap<>();
  
  public ListMultiMap(NoArgFunction<List<V>> listCreator) {
    this.listCreator = listCreator;
  }

  @Override
  public Collection<V> get(K key) {
    List<V> values = delegate.get(key);
    if (values == null) {
      values = Collections.emptyList();
    }
    return values;
  }
  
  @Override
  public Set<K> keySet() {
    return delegate.keySet();
  }
  
  @Override
  public MultiMap<K, V> put(K key, V value) {
    List<V> values = delegate.get(key);
    if (values == null) {
      values = listCreator.call();
    }
    values.add(value);
    delegate.put(key, values);
    return this;
  }
  
  // --------------------------------------------------------------------------
  
  /**
   * @return a new {@link LinkedList}-based {@link ListMultiMap}.
   */
  public static <K, V> ListMultiMap<K, V> createLinkedListMultiMap() {
    return new ListMultiMap<>(new NoArgFunction<List<V>>() {
      @Override
      public List<V> call() {
        return new LinkedList<>();
      }
    });
  }
  
  /**
   * @return a new {@link ArrayList}-based multimap.
   */
  public static <K, V> ListMultiMap<K, V> createArrayListMultiMap() {
    return new ListMultiMap<>(new NoArgFunction<List<V>>() {
      @Override
      public List<V> call() {
        return new ArrayList<>();
      }
    });
  }

  /**
   * @param the capacity used for the {@link ArrayList}s that are internally created.
   * @return a new {@link ArrayList}-based multimap.
   */
  public static <K, V> ListMultiMap<K, V> createArrayListMultiMap(final int capacity) {
    return new ListMultiMap<>(new NoArgFunction<List<V>>() {
      @Override
      public List<V> call() {
        return new ArrayList<>(capacity);
      }
    });
  }
}
