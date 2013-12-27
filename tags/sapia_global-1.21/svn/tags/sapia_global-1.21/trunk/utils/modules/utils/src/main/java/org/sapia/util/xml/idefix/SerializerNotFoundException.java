package org.sapia.util.xml.idefix;


// Import of Sapia's utility classes
// ---------------------------------
import org.sapia.util.CompositeException;


/**
 * Thrown when a serializer facotry could not create a serializer for a given type.
 *
 * @author <a href="mailto:jc@sapia-oss.org">Jean-Cedric Desrochers</a>
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class SerializerNotFoundException extends CompositeException {
  /**
   * Creates a new SerializerNotFoundException instance with the arguments passed in.
   *
   * @param aMessage The message describing the error.
   * @param aSourceError The source error to encapsulate.
   */
  public SerializerNotFoundException(String aMessage, Throwable aSourceError) {
    super(aMessage, aSourceError);
  }

  /**
   * Creates a new SerializerNotFoundException instance with the argument passed in.
   *
   * @param aMessage The message describing the error.
   */
  public SerializerNotFoundException(String aMessage) {
    super(aMessage);
  }
}
