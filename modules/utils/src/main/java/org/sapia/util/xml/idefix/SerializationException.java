package org.sapia.util.xml.idefix;


// Import of Sapia's utility classes
// ---------------------------------
import org.sapia.util.CompositeException;


/**
 * Thrown when a serializer could not process an object.
 *
 * @author <a href="mailto:jc@sapia-oss.org">Jean-Cedric Desrochers</a>
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class SerializationException extends CompositeException {
  /**
   * Creates a new SerializationException instance with the arguments passed in.
   *
   * @param aMessage The message describing the error.
   * @param aSourceError The source error to encapsulate.
   */
  public SerializationException(String aMessage, Throwable aSourceError) {
    super(aMessage, aSourceError);
  }

  /**
   * Creates a new SerializationException instance with the argument passed in.
   *
   * @param aMessage The message describing the error.
   */
  public SerializationException(String aMessage) {
    super(aMessage);
  }
}
