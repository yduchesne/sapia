package org.sapia.ubik.rmi.interceptor;


/**
 * Signals that an interceptor could not be registered for a given
 * event.
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class InvalidInterceptorException extends RuntimeException {
  
  static final long serialVersionUID = 1L;

  private Throwable _err;

  public InvalidInterceptorException(String msg) {
    super(msg);
  }

  public InvalidInterceptorException(Throwable err) {
    _err = err;
  }

  public InvalidInterceptorException(String msg, Throwable err) {
    super(msg);
    _err = err;
  }

  public void printStackTrace() {
    super.printStackTrace();

    if (_err != null) {
      System.err.println("NESTED EXCEPTION:");
      _err.printStackTrace();
    }
  }

  public void printStrackTrace(java.io.PrintStream ps) {
    super.printStackTrace(ps);

    if (_err != null) {
      ps.println("NESTED EXCEPTION:");
      _err.printStackTrace(ps);
    }
  }

  public void printStrackTrace(java.io.PrintWriter pw) {
    super.printStackTrace(pw);

    if (_err != null) {
      pw.println("NESTED EXCEPTION:");
      _err.printStackTrace(pw);
    }
  }

  public Throwable getNestedError() {
    return _err;
  }
}