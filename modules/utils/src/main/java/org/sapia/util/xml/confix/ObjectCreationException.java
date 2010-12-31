package org.sapia.util.xml.confix;


// Import of Sapia's utility classes
// ---------------------------------
import org.sapia.util.CompositeException;


/**
 * Thrown when an object factory could not create a given object.
 *
 * @author JC Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ObjectCreationException extends CompositeException {
  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

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
