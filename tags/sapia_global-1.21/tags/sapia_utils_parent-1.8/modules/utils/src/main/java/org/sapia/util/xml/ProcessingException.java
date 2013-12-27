package org.sapia.util.xml;

import org.sapia.util.CompositeException;


/**
 * This exception is thrown by a <code>ConfixProcessorIF</code> when the latter
 * cannot process a given XML configuration or by the <code>IdefixProcessorIF</code>
 * when it cannot process an object.
 *
 * @see org.sapia.util.xml.confix.ConfixProcessorIF
 * @see org.sapia.util.xml.idefix.IdefixProcessorIF
 * @author Jean-Cedric Desrochers
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ProcessingException extends Exception {
  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Creates a new ProcessingException instance with the arguments passed in.
   *
   * @param aMessage The message describing the error.
   * @param aSourceError The source error to encapsulate.
   */
  public ProcessingException(String aMessage, Throwable aSourceError) {
    super(aMessage, aSourceError);
  }

  /**
   * Creates a new ProcessingException instance with the argument passed in.
   *
   * @param aMessage The message describing the error.
   */
  public ProcessingException(String aMessage) {
    super(aMessage);
  }
}
