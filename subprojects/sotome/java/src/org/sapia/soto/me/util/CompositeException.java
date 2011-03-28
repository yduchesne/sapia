package org.sapia.soto.me.util;



/**
 * This class can be extended by other runtime exception classes that require
 * encapsulating another exception, and provide the latter's stack trace and
 * message instead of its own.
 *
 * @author Jean-Cedric Desrochers
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class CompositeException extends Exception {

  /** The source error that is encapsulated in this composite exception. */
  private Throwable _theSourceError;

  /**
   * Creates a new CompositeException instance with the arguments passed in.
   *
   * @param aMessage The message describing the error.
   * @param aSourceError The source error to encapsulate.
   */
  public CompositeException(String aMessage, Throwable aSourceError) {
    super(aMessage);
    _theSourceError = aSourceError;
  }

  /**
   * Creates a new CompositeException instance with the argument passed in.
   *
   * @param aMessage The message describing the error.
   */
  public CompositeException(String aMessage) {
    super(aMessage);
  }

  /**
   * Returns the source error encapsulated in this composite exception.
   *
   * @return The source error encapsulated in this composite exception.
   */
  public Throwable getSourceError() {
    return _theSourceError;
  }

  /**
   * Prints the stack trace of this composite exception to the standard error stream.
   */
  public void printStackTrace() {
    super.printStackTrace();

    if (_theSourceError != null) {
      System.err.print("\n---> NESTED EXCEPTION IS: ");
      _theSourceError.printStackTrace();
    }
  }
}
