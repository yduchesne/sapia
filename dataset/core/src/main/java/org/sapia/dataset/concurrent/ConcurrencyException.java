package org.sapia.dataset.concurrent;

import java.util.concurrent.ExecutionException;

/**
 * A runtime exception signaling a problem in the execution of an asynchronous task.
 *  
 * @author yduchesne
 *
 */
public class ConcurrencyException extends RuntimeException {
  
  private static final long serialVersionUID = 1L;
  
  /**
   * @param msg an error message.
   * @param e the original {@link Exception} that was thrown.
   */
  public ConcurrencyException(String msg, Exception e) {
    super(msg, e);
  }

  /**
   * @param msg an error message.
   * @param e a  {@link ConcurrentException} wrapping the original
   * exception that was thrown.
   */
  public ConcurrencyException(String msg, ExecutionException e) {
    super(msg, e.getCause());
  }

}
