package org.sapia.archie;


/**
 * Specifies the behavior of a factory of {@link Node}s.
 * 
 * @author Yanick Duchesne
 */
public interface NodeFactory {
  
  /**
   * @return a new {@link Node}.
   * @throws ProcessingException if a new node could not be created.
   */
  public Node newNode() throws ProcessingException;
}
