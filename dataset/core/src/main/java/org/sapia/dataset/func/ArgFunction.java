package org.sapia.dataset.func;

/**
 * Specifies generic function behavior.
 * 
 * @author yduchesne
 */
public interface ArgFunction<F, T> {
  
  /**
   * @param arg an input argument.
   * @return the output value.
   */
  public T call(F arg);

}
