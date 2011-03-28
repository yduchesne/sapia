package org.sapia.soto.me.xml;

import org.sapia.soto.me.util.CompositeException;


/**
 * This exception is thrown by a {@link J2meConfixProcessor} when the latter
 * cannot process a given XML configuration.
 *
 * @author Jean-Cedric Desrochers
 */
public class J2meProcessingException extends CompositeException {

  /**
   * Creates a new J2meProcessingException instance with the arguments passed in.
   *
   * @param aMessage The message describing the error.
   * @param aSourceError The source error to encapsulate.
   */
  public J2meProcessingException(String aMessage, Throwable aSourceError) {
    super(aMessage, aSourceError);
  }

  /**
   * Creates a new J2meProcessingException instance with the argument passed in.
   *
   * @param aMessage The message describing the error.
   */
  public J2meProcessingException(String aMessage) {
    super(aMessage);
  }
}
