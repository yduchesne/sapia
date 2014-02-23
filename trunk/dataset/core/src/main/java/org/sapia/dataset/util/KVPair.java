package org.sapia.dataset.util;

/**
 * Implements a key/value pair.
 * 
 * @author yduchesne
 *
 */
public class KVPair<K, V> {
  
  private K key;
  private V value;
  
  /**
   * @param key a key.
   * @param value a value.
   */
  public KVPair(K key, V value) {
    this.key   = key;
    this.value = value;
  }
  
  /**
   * @return this instance's key.
   */
  public K getKey() {
    return key;
  }
  
  /**
   * @return this instance's value.
   */
  public V getValue() {
    return value;
  }
  
  /**
   * @param key a key.
   * @param value a value.
   * @return a new instance of this class, wrapping the given parameters.
   */
  public static <K, V> KVPair<K, V> obj(K key, V value) {
    return new KVPair<K, V>(key, value);
  }
  
  @Override
  public int hashCode() {
    return Objects.safeHashCode(key, value);
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public boolean equals(Object obj) {
    if (obj instanceof KVPair) {
      KVPair<K, V> other = (KVPair<K, V>) obj;
      return Objects.safeEquals(this.key, other.key) 
          && Objects.safeEquals(this.value, other.value);
    }
    return false;
  }
  
  @Override
  public String toString() {
    return Strings.toString("key", key, "value", value);
  }

}
