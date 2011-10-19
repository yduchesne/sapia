/*
 * IllegalFormException.java
 *
 * Created on September 2, 2005, 8:51 PM
 *
 */

package org.sapia.soto.state.form;

/**
 * Thrown when a form with a given ID could not be found.
 *
 * @author yduchesne
 */
public class IllegalFormException extends RuntimeException {
  
  /** Creates a new instance of IllegalFormException */
  public IllegalFormException(String msg) {
    super(msg);
  }
  
}
