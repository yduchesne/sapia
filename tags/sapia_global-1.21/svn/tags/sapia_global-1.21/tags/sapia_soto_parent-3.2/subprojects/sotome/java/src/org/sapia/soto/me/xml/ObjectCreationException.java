package org.sapia.soto.me.xml;

import org.sapia.soto.me.util.CompositeException;

/**
 * Thrown when an object factory could not create a given object.
 *
 * @author Jean-Cedric Desrochers
 */
public class ObjectCreationException extends CompositeException {

  /**
   * Creates a new ObjectCreationException instance with the arguments passed in.
   *
   * @param aMessage The message describing the error.
   * @param aSourceError The source error to encapsulate.
   */
  public ObjectCreationException(String aMessage, Throwable aSourceError) {
    super(aMessage, aSourceError);
  }

  /**
   * Creates a new ObjectCreationException instance with the argument passed in.
   *
   * @param aMessage The message describing the error.
   */
  public ObjectCreationException(String aMessage) {
    super(aMessage);
  }
}
