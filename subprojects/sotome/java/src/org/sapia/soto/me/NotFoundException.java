package org.sapia.soto.me;

import org.sapia.soto.me.util.CompositeException;

/**
 * Thrown when an object is not found.
 */
public class NotFoundException extends CompositeException {
  /**
   * Constructor for ServiceNotFoundException.
   * 
   * @param arg0
   * @param arg1
   */
  public NotFoundException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  /**
   * Constructor for ServiceNotFoundException.
   * 
   * @param arg0
   */
  public NotFoundException(String arg0) {
    super(arg0);
  }
}
