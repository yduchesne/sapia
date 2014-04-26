package org.sapia.archie;


/**
 * Thrown when problem occurs during node manipulation.
 * 
 * @author Yanick Duchesne
 */
public class ProcessingException extends Exception {
  
  static final long serialVersionUID = 1L;

  public ProcessingException(String msg) {
    super(msg);
  }
  
  public ProcessingException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
