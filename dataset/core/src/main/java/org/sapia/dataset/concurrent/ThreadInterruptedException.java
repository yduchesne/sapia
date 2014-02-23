package org.sapia.dataset.concurrent;

/**
 * A runtime  exception wrapper for {@link InterruptedException}s.
 * 
 * @author yduchesne
 *
 */
public class ThreadInterruptedException extends RuntimeException {
  
  private static final long serialVersionUID = 1L;

  public ThreadInterruptedException(InterruptedException e) {
    super(e);
  }

}
