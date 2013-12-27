package org.sapia.soto.ubik.monitor.impl;

import org.sapia.soto.ServiceMetaData;
import org.sapia.soto.SotoContainer;
import org.sapia.soto.ubik.monitor.StatusResultList;
import org.sapia.soto.ubik.monitor.TestMonitoredService;
import org.sapia.soto.ubik.monitor.impl.CompositeMonitorService;

import junit.framework.TestCase;

public class CompositeMonitorServiceTest extends TestCase {

  public CompositeMonitorServiceTest(String arg0) {
    super(arg0);
  }
  
  public void testGetStatusForMonitor() throws Exception{
    TestMonitoredService service = new TestMonitoredService(true);
    ServiceMetaData meta = new ServiceMetaData(new SotoContainer(), "test", service);
    TestMonitorAgentLayer layer = new TestMonitorAgentLayer();
    layer.setInvoke("perform");
    layer.init(meta);
    TestDomainMonitorService monitor = new TestDomainMonitorService();
    monitor.addAgent(layer);
    CompositeMonitorService composite = new CompositeMonitorService();
    composite.addMonitor(monitor);
    StatusResultList lst = composite.getStatusForClass(TestMonitoredService.class.getName());
    super.assertEquals(1, lst.getResults().size());
    super.assertTrue(monitor.wasCalled);
    super.assertTrue(service.wasCalled);
    composite.getStatusForClass("test");
    service.wasCalled = false;
    composite.getStatusForClass(TestMonitoredService.class.getName());
    super.assertTrue(!service.wasCalled);
  }
  
  public void testGetStatusForAgent() throws Exception{
    TestMonitoredService service = new TestMonitoredService(true);
    ServiceMetaData meta = new ServiceMetaData(new SotoContainer(), "test", service);
    TestMonitorAgentLayer layer = new TestMonitorAgentLayer();
    layer.setInvoke("perform");
    layer.init(meta);
    CompositeMonitorService composite = new CompositeMonitorService();
    composite.addAgent(layer);
    StatusResultList lst = composite.getStatusForClass(TestMonitoredService.class.getName());
    super.assertEquals(1, lst.getResults().size());
    composite.getStatusForClass(TestMonitoredService.class.getName());
    super.assertTrue(service.wasCalled);
  }  
  
  public void testGetStatusForMonitorable() throws Exception{
    TestMonitoredService service = new TestMonitoredService(true);
    ServiceMetaData meta = new ServiceMetaData(new SotoContainer(), "test", service);
    TestMonitorAgentLayer layer = new TestMonitorAgentLayer();
    layer.init(meta);
    CompositeMonitorService composite = new CompositeMonitorService();
    composite.addAgent(layer);
    StatusResultList lst = composite.getStatusForClass(TestMonitoredService.class.getName());
    super.assertEquals(1, lst.getResults().size());
    composite.getStatusForClass(TestMonitoredService.class.getName());
    super.assertTrue(service.wasCalled);
  }  
  

}
