package org.sapia.validator;


/**
 * Models an error message. 
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ErrorMessage {
  private String _msg;
  private String _locale;
  private String _id;

  /**
   * Constructor for ErrorMessage.
   */
  public ErrorMessage() {
  }
  
  /**
   * Sets this message's identifier.
   * 
   * @param id an arbitrary identifier.
   */
  void setId(String id) {
    _id = id;
  }
  
  /**
   * Sets this message's string value.
   * 
   * @param this message's "string value".
   */
  public void setValue(String msg) {
    _msg = msg;
  }
  
  /**
   * Returns this message's string value.
   * 
   * @return this message's string value.
   */
  public String getValue() {
    return _msg;
  }

  /**
   * Returns this message's locale.
   * 
   * @return this message's locale, as a string.
   * 
   * @see #setLocale(String)
   */
  public String getLocale() {
    return _locale;
  }
  
  /**
   * Sets this message's locale.
   * 
   * @param this message's locale, as a path of the form: <code>language/country/variant</code>.
   */
  public void setLocale(String path) {
    _locale = path;
  }
  
  /**
   * @see Object#toString()
   */
  public String toString() {
    return "[ " + _msg + " ] ";
  }
}
