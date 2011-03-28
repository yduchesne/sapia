package org.sapia.soto.aop;

import org.sapia.soto.Service;

/**
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class TestService implements Service {
  public void firstMethod() {
  }

  public void firstMethod(String arg) {
  }

  public void secondMethod() {
  }

  public void throwsMethod() throws Exception {
    throw new Exception("test");
  }

  public String getName() {
    return "test";
  }

  /**
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
  }

  /**
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
  }

  /**
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {
  }
}
