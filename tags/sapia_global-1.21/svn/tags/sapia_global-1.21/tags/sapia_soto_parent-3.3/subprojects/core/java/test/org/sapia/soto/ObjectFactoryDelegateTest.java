package org.sapia.soto;

import java.io.File;

import junit.framework.TestCase;

public class ObjectFactoryDelegateTest extends TestCase {

  public ObjectFactoryDelegateTest(String arg0) {
    super(arg0);
  }

  public void testLoad() throws Exception{
    SotoContainer soto = new SotoContainer();
    soto.load(new File("etc/test/objectFactoryDelegate.xml"));
    soto.start();
    TestPOJO pojo = (TestPOJO)soto.lookup("pojo");
    TestObjectFactoryDelegate deleg = (TestObjectFactoryDelegate)soto.lookup("testDelegate");
    assertTrue(deleg.called);
  }

}
