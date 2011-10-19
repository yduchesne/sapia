package org.sapia.soto.state.cocoon.simple;

import java.util.Enumeration;

import org.apache.cocoon.environment.Session;

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
public class SimpleSession implements Session {

  private simple.http.session.Session _sess;

  public SimpleSession(simple.http.session.Session session) {
    _sess = session;
  }

  /**
   * @see org.apache.cocoon.environment.Session#getAttribute(java.lang.String)
   */
  public Object getAttribute(String name) {
    return _sess.get(name);
  }

  /**
   * @see org.apache.cocoon.environment.Session#getAttributeNames()
   */
  public Enumeration getAttributeNames() {
    return new EnumerationAdapter(_sess.keySet().iterator());
  }

  /**
   * @see org.apache.cocoon.environment.Session#getCreationTime()
   */
  public long getCreationTime() {
    return 0;
  }

  /**
   * @see org.apache.cocoon.environment.Session#getId()
   */
  public String getId() {
    return null;
  }

  /**
   * @see org.apache.cocoon.environment.Session#getLastAccessedTime()
   */
  public long getLastAccessedTime() {
    return 0;
  }

  /**
   * @see org.apache.cocoon.environment.Session#getMaxInactiveInterval()
   */
  public int getMaxInactiveInterval() {
    return 0;
  }

  /**
   * @see org.apache.cocoon.environment.Session#invalidate()
   */
  public void invalidate() {
    _sess.destroy();
  }

  /**
   * @see org.apache.cocoon.environment.Session#isNew()
   */
  public boolean isNew() {
    return false;
  }

  /**
   * @see org.apache.cocoon.environment.Session#removeAttribute(java.lang.String)
   */
  public void removeAttribute(String name) {
    _sess.remove(name);
  }

  /**
   * @see org.apache.cocoon.environment.Session#setAttribute(java.lang.String,
   *      java.lang.Object)
   */
  public void setAttribute(String name, Object val) {
    _sess.put(name, val);
  }

  /**
   * @see org.apache.cocoon.environment.Session#setMaxInactiveInterval(int)
   */
  public void setMaxInactiveInterval(int arg0) {
  }

}
