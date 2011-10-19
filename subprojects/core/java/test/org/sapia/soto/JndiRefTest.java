package org.sapia.soto;

import javax.naming.Context;

import junit.framework.TestCase;

import org.sapia.soto.config.JndiRef;
import org.sapia.soto.util.Param;

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
public class JndiRefTest extends TestCase {

  public JndiRefTest(String name) {
    super(name);
  }

  public void testOnCreate() throws Exception {
    JndiRef ref = new JndiRef();
    ref.setName("foo");
    Param p = ref.createProperty();
    p.setName(Context.INITIAL_CONTEXT_FACTORY);
    p.setValue(TestInitialContextFactory.class.getName());
    ref.onCreate();
  }

}
