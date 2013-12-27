package org.sapia.soto.jetty;

import java.io.File;

import org.sapia.soto.SotoContainer;

import junit.framework.TestCase;

public class JettyServiceImplTest extends TestCase {

  public JettyServiceImplTest(String arg0) {
    super(arg0);
  }

  public void testStartup() throws Exception{
    SotoContainer cont = new SotoContainer();
    cont.load(new File("etc/jetty.soto.xml"));
    cont.start();
    Thread.sleep(5000);
    cont.dispose();
  }
}
