package org.sapia.beeq.sender;

public class SenderException extends Exception {
  
  static final long serialVersionUID = 1L;

  public SenderException(String message) {
    super(message);
  }

  public SenderException(String message, Throwable cause) {
    super(message, cause);
  }

}
