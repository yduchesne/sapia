package org.sapia.soto;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

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
public class TestInitialContextFactory implements InitialContextFactory {

  /**
   * @see javax.naming.spi.InitialContextFactory#getInitialContext(java.util.Hashtable)
   */
  public Context getInitialContext(Hashtable environment)
      throws NamingException {
    return new TestContext();
  }

}
