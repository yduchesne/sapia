package org.sapia.beeq.sender;

/**
 * This exception is thrown when a message could not be send, for any
 * reason.
 *  
 * @author yduchesne
 *
 */
public class SenderException extends Exception {
  
  static final long serialVersionUID = 1L;

  public SenderException(String message) {
    super(message);
  }

  public SenderException(String message, Throwable cause) {
    super(message, cause);
  }

}
