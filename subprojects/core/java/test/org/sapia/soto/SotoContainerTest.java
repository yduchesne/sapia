package org.sapia.soto;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

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
public class SotoContainerTest extends TestCase {
  public SotoContainerTest(String arg0) {
    super(arg0);
  }
  public void testInit() throws Exception {
    SotoContainer cont = new SotoContainer();
    cont.load("org/sapia/soto/singleService.xml");

    TestService svc = (TestService) cont.lookup("some/service");
    super.assertTrue("Service was not initialized", svc.init);
    super.assertTrue("Service should not be in started mode", !svc.started);
  }
  public void testStart() throws Exception {
    SotoContainer cont = new SotoContainer();
    cont.load("org/sapia/soto/singleService.xml");
    cont.start();

    TestService svc = (TestService) cont.lookup("some/service");
    super.assertTrue("Service was not started", svc.started);
  }

  public void testDispose() throws Exception {
    SotoContainer cont = new SotoContainer();
    cont.load("org/sapia/soto/singleService.xml");
    cont.start();

    TestService svc = (TestService) cont.lookup("some/service");
    cont.dispose();
    super.assertTrue("Service was not stopped", svc.disposed);
  }
  
  public void testBootstrap() throws Exception {
    SotoContainer cont = new SotoContainer();
    Map vars = new HashMap();
    vars.put("soto.bootstrap", "resource:/org/sapia/soto/bootstrappedService.xml");
    cont.load("org/sapia/soto/singleService.xml", vars);
    cont.start();
    cont.lookup("bootstrapped");
  } 
  
  public void testInitPOJO() throws Exception {
    SotoContainer cont = new SotoContainer();
    cont.load(new File("etc/test/pojoService.xml"));
    TestPOJO pojo = (TestPOJO) cont.lookup("pojo");
    super.assertTrue("POJO was not initialized", pojo.init);
  }
  
  public void testStartPOJO() throws Exception {
    SotoContainer cont = new SotoContainer();
    cont.load(new File("etc/test/pojoService.xml"));
    cont.start();
    TestPOJO pojo = (TestPOJO) cont.lookup("pojo");
    super.assertTrue("POJO was not started", pojo.start);
  }

  public void testDisposePOJO() throws Exception {
    SotoContainer cont = new SotoContainer();
    cont.load(new File("etc/test/pojoService.xml"));
    cont.start();
    TestPOJO pojo = (TestPOJO) cont.lookup("pojo");
    cont.dispose();
    super.assertTrue("POJO was not disposed", pojo.dispose);
  }
  
  public void testPackageMapping() throws Exception {
    SotoContainer cont = new SotoContainer();
    cont.load(new File("etc/test/packageMapping.xml"));
    cont.start();
    cont.lookup("typeService");
  }  
  
  public void testAny() throws Exception {
    SotoContainer cont = new SotoContainer();
    cont.load(new File("etc/test/any.xml"));
    cont.start();
    TestPOJO pojo = (TestPOJO) cont.lookup("pojo");
    super.assertTrue(pojo.xmlAware);
  }    

  public void testLookupInstanceOf() throws Exception {
    SotoContainer cont = new SotoContainer();
    TestService svc = new TestService();
    ServiceMetaData data = new ServiceMetaData(new SotoContainer(), "test", svc, new ArrayList());
    cont.bind(data);
    svc = (TestService) cont.lookup(Service.class);
    try {
      cont.lookup(Object.class);
      throw new Exception("Matching should only be done on interfaces");
    } catch(Exception e) {
      //ok
    }

    cont.start();
    svc = (TestService) cont.lookup(Service.class);
  }
  
  public void testBind() throws Exception {
    SotoContainer cont = new SotoContainer();

    try {
      cont.load("org/sapia/soto/duplicateService.xml");
      throw new Exception("Two services with same ID should not be allowed");
    } catch(Exception e) {
      //ok
    }
  }

  public void testAssociation() throws Exception {
    SotoContainer cont = new SotoContainer();
    cont.load("org/sapia/soto/association.xml");
    cont.start();

    TestService svc = (TestService) cont.lookup("some/service");
    super.assertTrue("Associated service is null", svc.child != null);
    svc = (TestService) cont.lookup("some/other/service");
    super.assertTrue("Associated service is null", svc.child != null);
  }

  public void testLayer() throws Exception {
    SotoContainer cont = new SotoContainer();
    cont.load("org/sapia/soto/layer.xml");
    super.assertTrue("Layer not initialized", TestLayer.init);
    cont.start();
    super.assertTrue("Layer not started", TestLayer.started);
  }
  
  public void testUriDefs() throws Exception {
    SotoContainer cont = new SotoContainer();
    System.setProperty("soto.debug", "true");
    cont.load("org/sapia/soto/uriService.xml");
  } 

}
