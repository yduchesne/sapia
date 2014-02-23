package org.sapia.dataset.util;

/**
 * A default {@link Ref} implementation.
 * 
 * @author yduchesne
 *
 */
public class DefaultRef<T> implements Ref<T>{
  
  private T value;
  
  /** 
   * @param value the value to wrap.
   */
  public DefaultRef(T value) {
    this.value = value;
  }
  
  @Override
  public T get() {
    return value;
  }

}
