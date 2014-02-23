package org.sapia.dataset.func;

/**
 * Specifies a function that takes no argument.
 * 
 * @author yduchesne
 *
 */
public interface NoArgFunction<R> {
  
  /**
   * @return a value.
   */
  public R call();

}
