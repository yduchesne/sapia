package org.sapia.soto.ubik.monitor.impl;

import java.util.Properties;

import org.sapia.soto.ServiceMetaData;
import org.sapia.soto.SotoContainer;
import org.sapia.soto.ubik.monitor.TestMonitoredService;

import junit.framework.TestCase;

public class AbstractMonitorAgentLayerTest extends TestCase {

  public AbstractMonitorAgentLayerTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }
  
  public void testCheckStatus() throws Exception{
    TestMonitoredService service = new TestMonitoredService(true);
    ServiceMetaData meta = new ServiceMetaData(new SotoContainer(), "test", service);
    TestMonitorAgentLayer layer = new TestMonitorAgentLayer();
    layer.setInvoke("perform");
    layer.init(meta);
    assertTrue(layer.checkStatus().isError());
  }
  
  public void testCheckStatusWithArgs() throws Exception{
    TestMonitoredService service = new TestMonitoredService(true);
    ServiceMetaData meta = new ServiceMetaData(new SotoContainer(), "test", service);
    TestMonitorAgentLayer layer = new TestMonitorAgentLayer();
    AbstractMonitorAgentLayer.MethodDescriptor desc = layer.createInvoke();
    desc.setName("perform");
    AbstractMonitorAgentLayer.Arg arg = desc.createArg();
    arg.setValue("test");
    arg.setType(String.class);
    layer.init(meta);
    assertTrue(layer.checkStatus().isError());
  }  
  

  static class TestMonitorAgentLayer extends AbstractMonitorAgentLayer{
   
    protected Properties postProcess(Properties result) {
      return result;    
    }
  }
}
