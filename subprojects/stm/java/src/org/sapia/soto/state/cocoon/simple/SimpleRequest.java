package org.sapia.soto.state.cocoon.simple;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.cocoon.environment.Cookie;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;

import simple.http.Profile;
import simple.http.session.Manager;
import simple.util.parse.URIParser;

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
public class SimpleRequest implements Request, Consts {

  private static final Enumeration EMPTY_ENUM = new EmptyEnum();

  private simple.http.Request      _req;
  private String                   _serverName, _contextPath;
  private URIParser                _uri;
  private int                      _serverPort;

  public SimpleRequest(simple.http.Request req, String contextPath,
      URIParser uri, String serverName, int serverPort) {
    _req = req;
    _uri = uri;
    _uri.parse(req.getURI());
    _contextPath = contextPath;
    _serverName = serverName;
    _serverPort = serverPort;
  }

  public simple.http.Request getInternalRequest() {
    return _req;
  }

  /**
   * @see org.apache.cocoon.environment.Request#get(java.lang.String)
   */
  public Object get(String name) {
    return _req.getAttributes().getAttribute(name);
  }

  /**
   * @see org.apache.cocoon.environment.Request#getAttribute(java.lang.String)
   */
  public Object getAttribute(String name) {
    return _req.getAttributes().getAttribute(name);
  }

  /**
   * @see org.apache.cocoon.environment.Request#getAttributeNames()
   */
  public Enumeration getAttributeNames() {
    return _req.getAttributes().getAttributeNames();
  }

  /**
   * @see org.apache.cocoon.environment.Request#getAuthType()
   */
  public String getAuthType() {
    return null;
  }

  /**
   * @see org.apache.cocoon.environment.Request#getCharacterEncoding()
   */
  public String getCharacterEncoding() {
    return null;
  }

  /**
   * @see org.apache.cocoon.environment.Request#getContentLength()
   */
  public int getContentLength() {
    return _req.getContentLength();
  }

  /**
   * @see org.apache.cocoon.environment.Request#getContentType()
   */
  public String getContentType() {
    return _req.getValue(CONTENT_TYPE);
  }

  /**
   * @see org.apache.cocoon.environment.Request#getContextPath()
   */
  public String getContextPath() {
    return _contextPath;
  }

  /**
   * @see org.apache.cocoon.environment.Request#getCookieMap()
   */
  public Map getCookieMap() {
    Cookie[] cookies = getCookies();
    Map cookieMap = new HashMap();
    for(int i = 0; i < cookies.length; i++) {
      cookieMap.put(cookies[i].getName(), cookies[i]);
    }
    return cookieMap;
  }

  /**
   * @see org.apache.cocoon.environment.Request#getCookies()
   */
  public Cookie[] getCookies() {
    return new Cookie[0];
  }

  /**
   * @see org.apache.cocoon.environment.Request#getDateHeader(java.lang.String)
   */
  public long getDateHeader(String arg) {
    return _req.getDate(arg);
  }

  /**
   * @see org.apache.cocoon.environment.Request#getHeader(java.lang.String)
   */
  public String getHeader(String name) {
    return _req.getValue(name);
  }

  /**
   * @see org.apache.cocoon.environment.Request#getHeaderNames()
   */
  public Enumeration getHeaderNames() {
    return EMPTY_ENUM;
  }

  /**
   * @see org.apache.cocoon.environment.Request#getHeaders(java.lang.String)
   */
  public Enumeration getHeaders(String name) {
    return new ArrayEnum(_req.getValues(name));
  }

  /**
   * @see org.apache.cocoon.environment.Request#getLocale()
   */
  public Locale getLocale() {
    return _req.getLanguage();
  }

  /**
   * @see org.apache.cocoon.environment.Request#getLocales()
   */
  public Enumeration getLocales() {
    return new ArrayEnum(new Locale[] { _req.getLanguage() });
  }

  /**
   * @see org.apache.cocoon.environment.Request#getMethod()
   */
  public String getMethod() {
    return _req.getMethod();
  }

  /**
   * @see org.apache.cocoon.environment.Request#getParameter(java.lang.String)
   */
  public String getParameter(String name) {
    try {
      return _req.getParameter(name);
    } catch(IOException e) {
      throw new IllegalStateException("Could not acquire request parameter '"
          + name + "': " + e.getMessage());
    }
  }

  /**
   * @see org.apache.cocoon.environment.Request#getParameterNames()
   */
  public Enumeration getParameterNames() {
    try {
      return _req.getParameters().getParameterNames();
    } catch(IOException e) {
      throw new IllegalStateException(
          "Could not acquire request parameter names: " + e.getMessage());
    }
  }

  /**
   * @see org.apache.cocoon.environment.Request#getParameterValues(java.lang.String)
   */
  public String[] getParameterValues(String name) {
    try {
      return _req.getParameters().getParameters(name);
    } catch(IOException e) {
      throw new IllegalStateException(
          "Could not acquire request parameter names: " + e.getMessage());
    }
  }

  /**
   * @see org.apache.cocoon.environment.Request#getPathInfo()
   */
  public String getPathInfo() {
    return _uri.getPath().getPath();
  }

  /**
   * @see org.apache.cocoon.environment.Request#getPathTranslated()
   */
  public String getPathTranslated() {
    return null;
  }

  /**
   * @see org.apache.cocoon.environment.Request#getProtocol()
   */
  public String getProtocol() {
    return "HTTP/" + _req.getMajor() + "." + _req.getMinor();
  }

  /**
   * @see org.apache.cocoon.environment.Request#getQueryString()
   */
  public String getQueryString() {
    try {
      return _req.getParameters().toString();
    } catch(IOException e) {
      throw new IllegalStateException(
          "Could not acquire request parameter names: " + e.getMessage());
    }
  }

  /**
   * @see org.apache.cocoon.environment.Request#getRemoteAddr()
   */
  public String getRemoteAddr() {
    return _req.getInetAddress().getHostAddress();
  }

  /**
   * @see org.apache.cocoon.environment.Request#getRemoteHost()
   */
  public String getRemoteHost() {
    return _req.getInetAddress().getHostName();
  }

  /**
   * @see org.apache.cocoon.environment.Request#getRemoteUser()
   */
  public String getRemoteUser() {
    return null;
  }

  /**
   * @see org.apache.cocoon.environment.Request#getRequestedSessionId()
   */
  public String getRequestedSessionId() {
    return null;
  }

  /**
   * @see org.apache.cocoon.environment.Request#getRequestURI()
   */
  public String getRequestURI() {
    return _uri.toString();
  }

  /**
   * @see org.apache.cocoon.environment.Request#getScheme()
   */
  public String getScheme() {
    return _uri.getScheme();
  }

  /**
   * @see org.apache.cocoon.environment.Request#getServerName()
   */
  public String getServerName() {
    return _serverName;
  }

  /**
   * @see org.apache.cocoon.environment.Request#getServerPort()
   */
  public int getServerPort() {
    return _serverPort;
  }

  /**
   * @see org.apache.cocoon.environment.Request#getServletPath()
   */
  public String getServletPath() {
    return "";
  }

  /**
   * @see org.apache.cocoon.environment.Request#getSession()
   */
  public Session getSession() {
    simple.http.session.Session sess = _req.getSession();
    if(sess != null) {
      return new SimpleSession(_req.getSession());
    }
    return null;
  }

  /**
   * @see org.apache.cocoon.environment.Request#getSession(boolean)
   */
  public Session getSession(boolean create) {
    simple.http.session.Session sess = _req.getSession();
    if(sess != null) {
      return new SimpleSession(sess);
    }
    if(create) {
      Profile profile = _req.getProfile();
      sess = Manager.getSession(profile);
      if(sess != null) {
        return new SimpleSession(sess);
      }
    }
    return null;
  }

  /**
   * @see org.apache.cocoon.environment.Request#getSitemapURI()
   */
  public String getSitemapURI() {
    return null;
  }

  /**
   * @see org.apache.cocoon.environment.Request#getUserPrincipal()
   */
  public Principal getUserPrincipal() {
    return null;
  }

  /**
   * @see org.apache.cocoon.environment.Request#isRequestedSessionIdFromCookie()
   */
  public boolean isRequestedSessionIdFromCookie() {
    return false;
  }

  /**
   * @see org.apache.cocoon.environment.Request#isRequestedSessionIdFromURL()
   */
  public boolean isRequestedSessionIdFromURL() {
    return false;
  }

  /**
   * @see org.apache.cocoon.environment.Request#isRequestedSessionIdValid()
   */
  public boolean isRequestedSessionIdValid() {
    return false;
  }

  /**
   * @see org.apache.cocoon.environment.Request#isSecure()
   */
  public boolean isSecure() {
    return false;
  }

  /**
   * @see org.apache.cocoon.environment.Request#isUserInRole(java.lang.String)
   */
  public boolean isUserInRole(String arg0) {
    return false;
  }

  /**
   * @see org.apache.cocoon.environment.Request#removeAttribute(java.lang.String)
   */
  public void removeAttribute(String name) {
    _req.getAttributes().removeAttribute(name);
  }

  /**
   * @see org.apache.cocoon.environment.Request#setAttribute(java.lang.String,
   *      java.lang.Object)
   */
  public void setAttribute(String name, Object obj) {
    _req.getAttributes().setAttribute(name, obj);
  }

  /**
   * @see org.apache.cocoon.environment.Request#setCharacterEncoding(java.lang.String)
   */
  public void setCharacterEncoding(String arg0)
      throws UnsupportedEncodingException {
  }
  
  @Override
  public String getSitemapURIPrefix() {
    return null;
  }

}
