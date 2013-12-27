package org.sapia.util.xml.confix;


// ---------------------------------
/**
 * Thrown when an XML processor could not complete the processing of
 * a XML configuration.
 *
 * @author Jean-Cedric Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ConfigurationException extends Exception {
  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Creates a new ConfigurationException instance with the arguments passed in.
   *
   * @param aMessage The message describing the error.
   * @param aSourceError The source error to encapsulate.
   */
  public ConfigurationException(String aMessage, Throwable aSourceError) {
    super(aMessage, aSourceError);
  }

  /**
   * Creates a new ConfigurationException instance with the argument passed in.
   *
   * @param aMessage The message describing the error.
   */
  public ConfigurationException(String aMessage) {
    super(aMessage);
  }
}
