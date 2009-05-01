package org.sapia.cocoon.generation.chunk.template;

/**
 * Extends the {@link Node} interface, and specifies behavior for adding
 * child nodes.
 * 
 * @author yduchesne
 *
 */
public interface ComplexNode extends Node{
  
  public void addChild(Node node);

}
