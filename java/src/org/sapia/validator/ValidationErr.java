package org.sapia.validator;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * An instance of this class represents a validation error.
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ValidationErr {
  private String _id;
  private Object _msg;
  
  /**
   * Constructor for ValidationErr.
   */  
  ValidationErr(String id, Throwable message) {
    _id    = id;
    _msg   = message;
  }

  /**
   * Constructor for ValidationErr.
   */
  ValidationErr(String id, String message) {
    _id    = id;
    _msg   = message;
  }

  /**
   * Returns the identifier of the rule that failed.
   *
   * @return the identifier of a <code>Rule</code>.
   */
  public String getId() {
    return _id;
  }

  /**
   * Returns the message of this error. If this instance encapsulates
   * a <code>Throwable</code>, then that instance's message is returned.
   *
   * @return a message, or <code>null</code> if no message was set.
   */
  public String getMsg() {
    if(_msg != null){
      if(_msg instanceof Throwable){
        return ((Throwable)_msg).getMessage();
      }
      return (String)_msg;
    }
    else{
      return null;
    }
  }

  /**
   * Returns the <code>Throwable</code> of this instance, if
   * the latter was indeed constructed with a Throwable.
   *
   * @return a <code>Throwable</code>.
   */
  public Throwable getThrowable() {
    return (Throwable) _msg;
  }

  /**
   * Returns <code>true</code> if this instance was constructed with
   * a <code>Throwable</code>.
   *
   * @return <code>true</code> if this instance was constructed with
   * a <code>Throwable</code>.
   */
  public boolean isThrowable() {
    return _msg instanceof Throwable;
  }
  
  /**
   * Returns the stack trace of the <code>Throwable</code> that this instance
   * encapsulates. Client applications should ensure that the internal error object
   * is indeed a <code>Throwable</code> by calling the <code>isThrowable()</code> method
   * on this instance.
   * 
   * @see #isThrowable().
   * 
   * @return the stacktrace of this instance's internal <code>Throwable</code>,
   *  as a string.
   */
  public String getStackTrace(){
    if(!isThrowable()){
      throw new IllegalStateException("Internal error is not an instance of " + Throwable.class.getName());
      
    }
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    Throwable t = (Throwable)_msg;
    t.printStackTrace(pw);
    pw.close();
    return sw.toString();
  }
}
