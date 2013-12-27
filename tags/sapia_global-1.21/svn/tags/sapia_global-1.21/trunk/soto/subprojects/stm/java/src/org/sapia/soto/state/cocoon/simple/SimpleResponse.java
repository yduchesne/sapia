package org.sapia.soto.state.cocoon.simple;

import java.util.Locale;

import org.apache.cocoon.environment.Cookie;
import org.apache.cocoon.environment.Response;

/**
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class SimpleResponse implements Response {

  private simple.http.Response _resp;
  private Locale               _locale;

  public SimpleResponse(Locale locale, simple.http.Response resp) {
    _resp = resp;
    _locale = locale;
  }

  simple.http.Response getInternalResponse() {
    return _resp;
  }

  /**
   * @see org.apache.cocoon.environment.Response#addCookie(org.apache.cocoon.environment.Cookie)
   */
  public void addCookie(Cookie cookie) {
  }

  /**
   * @see org.apache.cocoon.environment.Response#addDateHeader(java.lang.String,
   *      long)
   */
  public void addDateHeader(String name, long date) {
    _resp.addDate(name, date);
  }

  /**
   * @see org.apache.cocoon.environment.Response#addHeader(java.lang.String,
   *      java.lang.String)
   */
  public void addHeader(String name, String val) {
    _resp.add(name, val);
  }

  /**
   * @see org.apache.cocoon.environment.Response#addIntHeader(java.lang.String,
   *      int)
   */
  public void addIntHeader(String name, int val) {
    _resp.add(name, val);
  }

  /**
   * @see org.apache.cocoon.environment.Response#containsHeader(java.lang.String)
   */
  public boolean containsHeader(String name) {
    return _resp.contains(name);
  }

  /**
   * @see org.apache.cocoon.environment.Response#createCookie(java.lang.String,
   *      java.lang.String)
   */
  public Cookie createCookie(String name, String val) {
    return new SimpleCookie(new simple.util.net.Cookie(name, val));
  }

  /**
   * @see org.apache.cocoon.environment.Response#encodeURL(java.lang.String)
   */
  public String encodeURL(String url) {
    return url;
  }

  /**
   * @see org.apache.cocoon.environment.Response#getCharacterEncoding()
   */
  public String getCharacterEncoding() {
    return null;
  }

  /**
   * @see org.apache.cocoon.environment.Response#getLocale()
   */
  public Locale getLocale() {
    return _locale;
  }

  /**
   * @see org.apache.cocoon.environment.Response#setDateHeader(java.lang.String,
   *      long)
   */
  public void setDateHeader(String name, long date) {
    _resp.addDate(name, date);
  }

  /**
   * @see org.apache.cocoon.environment.Response#setHeader(java.lang.String,
   *      java.lang.String)
   */
  public void setHeader(String name, String value) {
    _resp.add(name, value);
  }

  /**
   * @see org.apache.cocoon.environment.Response#setIntHeader(java.lang.String,
   *      int)
   */
  public void setIntHeader(String name, int val) {
    _resp.add(name, val);
  }

  /**
   * @see org.apache.cocoon.environment.Response#setLocale(java.util.Locale)
   */
  public void setLocale(Locale locale) {
    _locale = locale;
  }

}
