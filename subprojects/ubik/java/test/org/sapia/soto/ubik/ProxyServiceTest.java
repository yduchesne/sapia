/*
 * ProxyServiceTest.java
 * JUnit based test
 *
 * Created on August 19, 2005, 8:58 AM
 */

package org.sapia.soto.ubik;

import org.sapia.soto.Service;

import junit.framework.TestCase;

/**
 *
 * @author yduchesne
 */
public class ProxyServiceTest extends TestCase {
  
  public ProxyServiceTest(String testName) {
    super(testName);
  }

  public void testSyncOnCreate() throws Exception{
    TestNamingService service = new TestNamingService();
    service.bind("test", new TestObject());
    ProxyService proxy = new ProxyService();
    proxy.addImplements(TestInterface.class.getName());
    proxy.setJndiName("test");
    proxy.setNamingService(service);
    TestInterface o = (TestInterface)proxy.onCreate();
    ((Service) o).init();
    ((Service) o).start();
    super.assertTrue(o.call());
    ((Service) o).dispose();
  }  
  
  public void testASyncOnCreate() throws Exception{
    TestNamingService service = new TestNamingService();
    ProxyService proxy = new ProxyService();
    proxy.addImplements(TestInterface.class.getName());
    proxy.setJndiName("test");
    proxy.setNamingService(service);
    TestInterface o = (TestInterface)proxy.onCreate();
    ((Service) o).init();
    ((Service) o).start();
    try{
      o.call();
      fail("Should not have been able to call method on proxy");
    }catch(Exception e){
      //ok
    }
    service.bind("test", new TestObject());
    super.assertTrue(o.call());
    super.assertEquals(0, service.getDiscoListeners().size());
    ((Service) o).dispose();
  }    
  
  public static interface TestInterface{
    public boolean call();
  }
  
  public static class TestObject implements TestInterface{
    public boolean call(){
      return true;
    }
  }  

}
