/*
 * ServiceMetaDataTest.java
 * JUnit based test
 *
 * Created on July 27, 2005, 2:31 PM
 */

package org.sapia.soto;

import junit.framework.TestCase;

import org.sapia.soto.config.MethodConfig;

/**
 *
 * @author yduchesne
 */
public class ServiceMetaDataTest extends TestCase {
  
  public ServiceMetaDataTest(String testName) {
    super(testName);
  }
  
  public void testInitPOJO() throws Exception{
    TestPOJO pojo = new TestPOJO();
    ServiceMetaData meta = new ServiceMetaData(new SotoContainer(), "test", pojo);
    MethodConfig config = new MethodConfig();
    config.setName("doInit");
    meta.setInitMethod(config);
    meta.init();
    super.assertTrue(pojo.init);
  }
  
  public void testStartPOJO() throws Exception{
    TestPOJO pojo = new TestPOJO();
    ServiceMetaData meta = new ServiceMetaData(new SotoContainer(), "test", pojo);
    MethodConfig config = new MethodConfig();
    config.setName("doStart");
    meta.setInitMethod(config);
    meta.init();
    super.assertTrue(pojo.start);
  }  
  
  public void testDisposePOJO() throws Exception{
    TestPOJO pojo = new TestPOJO();
    ServiceMetaData meta = new ServiceMetaData(new SotoContainer(), "test", pojo);
    MethodConfig config = new MethodConfig();
    config.setName("doDispose");
    meta.setInitMethod(config);
    meta.init();
    super.assertTrue(pojo.dispose);
  }    


  
}
