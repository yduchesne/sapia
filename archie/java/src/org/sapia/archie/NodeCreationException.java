package org.sapia.archie;


/**
 * Thrown when a {@link Node} could not be created.
 * 
 * @author Yanick Duchesne
 */
public class NodeCreationException extends ProcessingException {
  
  static final long serialVersionUID = 1L;
  
  public NodeCreationException(String msg) {
    super(msg);
  }
  
  public NodeCreationException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
