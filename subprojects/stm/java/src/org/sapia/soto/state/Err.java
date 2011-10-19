package org.sapia.soto.state;

import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * Holds error information pertaining to a state's execution.
 * 
 * @see org.sapia.soto.state.Result#error(Err)
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
public class Err {
  private String    _msg;
  private Throwable _err;

  public Err(String msg, Throwable t) {
    _msg = msg;
    _err = t;
  }

  public Err(String msg) {
    _msg = msg;
  }

  public Err(Throwable err) {
    _err = err;
  }

  /**
   * @return the <code>Throwable</code> that this instance holds, or
   *         <code>null</code> if this instance has no <code>Throwable</code>.
   */
  public Throwable getThrowable() {
    return _err;
  }

  /**
   * @return the <code>Throwable</code> kept within this instance as a string,
   *         or <code>null</code> if this instance holds not throwable.
   */
  public String getThrowableAsString() {
    if(_err == null) {
      return "";
    }

    return ExceptionUtils.getStackTrace(_err);
  }

  /**
   * @return the error message that this instance holds.
   */
  public String getMsg() {
    if(_msg == null && _err != null){
      return _err.getMessage();
    }
    return _msg;
  }

  public String toString() {
    return "[err="
        + (_err != null ? _err.getClass() + "-" + _err.getMessage() : "null")
        + ", msg=" + _msg + "]";
  }
}
