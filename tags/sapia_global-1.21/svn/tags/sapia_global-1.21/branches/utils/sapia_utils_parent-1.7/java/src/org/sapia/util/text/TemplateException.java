package org.sapia.util.text;


// Import of Sapia's utility classes
// ---------------------------------
import org.sapia.util.CompositeException;


/**
 * Thrown when some exception occurs when processing data to render.
 *
 * @author JC Desrochers.
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class TemplateException extends CompositeException {
  /**
   * Creates a new TemplateException instance with the arguments passed in.
   *
   * @param aMessage The message describing the error.
   * @param aSourceError The source error to encapsulate.
   */
  public TemplateException(String aMessage, Throwable aSourceError) {
    super(aMessage, aSourceError);
  }

  /**
   * Creates a new TemplateException instance with the argument passed in.
   *
   * @param aMessage The message describing the error.
   */
  public TemplateException(String aMessage) {
    super(aMessage);
  }
}
