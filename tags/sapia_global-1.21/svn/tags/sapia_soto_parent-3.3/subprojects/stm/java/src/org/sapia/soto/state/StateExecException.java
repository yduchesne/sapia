package org.sapia.soto.state;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Thrown when a state's execution has generated in an error.
 * 
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2003 <a
 *         href="http://www.sapia-oss.org">Sapia Open Source Software </a>. All
 *         Rights Reserved.</dd>
 *         </dt>
 *         <dt><b>License: </b>
 *         <dd>Read the license.txt file of the jar or visit the <a
 *         href="http://www.sapia-oss.org/license.html">license page </a> at the
 *         Sapia OSS web site</dd>
 *         </dt>
 *         </dl>
 */
public class StateExecException extends Exception {
  private Err _err;
  private boolean _abort;

  public StateExecException(Err err) {
    super(err != null && err.getMsg() != null ? err.getMsg() : "null");
    _err = err;
    if(_err == null) {
      Exception e = new Exception("Unspecified exception caught");
      _err = new Err(e);
    }
  }
  
  StateExecException abort(){
    _abort = true;
    return this;
  }
  
  boolean isAbort(){
    return _abort;
  }

  /**
   * @return the <code>Err</code> instance that holds error data.
   */
  public Err getErr() {
    return _err;
  }

  /**
   * @see java.lang.Throwable#printStackTrace()
   */
  public void printStackTrace() {
    printStackTrace(System.err);
  }

  /**
   * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
   */
  public void printStackTrace(PrintStream s) {
    if(_err.getThrowable() == null) {
      super.printStackTrace(s);
    } else {
      if(_err.getMsg() != null) {
        s.println(_err.getMsg());
      }

      _err.getThrowable().printStackTrace(s);
    }
  }

  /**
   * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
   */
  public void printStackTrace(PrintWriter s) {
    if(_err.getThrowable() == null) {
      super.printStackTrace(s);
    } else {
      if(_err.getMsg() != null) {
        s.print(_err.getMsg());
      }

      _err.getThrowable().printStackTrace(s);
    }
  }
}
