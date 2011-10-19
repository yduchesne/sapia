package org.sapia.soto.state.cocoon.simple;

import org.apache.cocoon.environment.Cookie;

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
public class SimpleCookie implements Cookie {

  private simple.util.net.Cookie _cookie;

  public SimpleCookie(simple.util.net.Cookie cookie) {
    _cookie = cookie;
  }

  /**
   * @see org.apache.cocoon.environment.Cookie#getComment()
   */
  public String getComment() {
    return null;
  }

  /**
   * @see org.apache.cocoon.environment.Cookie#getDomain()
   */
  public String getDomain() {
    return _cookie.getDomain();
  }

  /**
   * @see org.apache.cocoon.environment.Cookie#getMaxAge()
   */
  public int getMaxAge() {
    return _cookie.getExpiry();
  }

  /**
   * @see org.apache.cocoon.environment.Cookie#getName()
   */
  public String getName() {
    return _cookie.getName();
  }

  /**
   * @see org.apache.cocoon.environment.Cookie#getPath()
   */
  public String getPath() {
    return _cookie.getPath();
  }

  /**
   * @see org.apache.cocoon.environment.Cookie#getSecure()
   */
  public boolean getSecure() {
    return _cookie.getSecure();
  }

  /**
   * @see org.apache.cocoon.environment.Cookie#getValue()
   */
  public String getValue() {
    return _cookie.getValue();
  }

  /**
   * @see org.apache.cocoon.environment.Cookie#getVersion()
   */
  public int getVersion() {
    return _cookie.getVersion();
  }

  /**
   * @see org.apache.cocoon.environment.Cookie#setComment(java.lang.String)
   */
  public void setComment(String arg0) {
  }

  /**
   * @see org.apache.cocoon.environment.Cookie#setDomain(java.lang.String)
   */
  public void setDomain(String domain) {
    _cookie.setDomain(domain);
  }

  /**
   * @see org.apache.cocoon.environment.Cookie#setMaxAge(int)
   */
  public void setMaxAge(int max) {
    _cookie.setExpiry(max);
  }

  /**
   * @see org.apache.cocoon.environment.Cookie#setPath(java.lang.String)
   */
  public void setPath(String path) {
    _cookie.setPath(path);
  }

  /**
   * @see org.apache.cocoon.environment.Cookie#setSecure(boolean)
   */
  public void setSecure(boolean secure) {
    _cookie.setSecure(secure);
  }

  /**
   * @see org.apache.cocoon.environment.Cookie#setValue(java.lang.String)
   */
  public void setValue(String value) {
    _cookie.setValue(value);
  }

  /**
   * @see org.apache.cocoon.environment.Cookie#setVersion(int)
   */
  public void setVersion(int v) {
    _cookie.setVersion(v);
  }

}
