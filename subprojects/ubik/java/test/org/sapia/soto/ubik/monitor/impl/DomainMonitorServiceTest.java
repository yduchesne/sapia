package org.sapia.soto.ubik.monitor.impl;

import java.io.File;

import junit.framework.TestCase;

import org.sapia.soto.ServiceMetaData;
import org.sapia.soto.SotoContainer;
import org.sapia.soto.ubik.example.MonitoredService;
import org.sapia.soto.ubik.monitor.StatusResult;
import org.sapia.soto.ubik.monitor.StatusResultList;
import org.sapia.soto.ubik.monitor.TestMonitoredService;

public class DomainMonitorServiceTest extends TestCase {

  public DomainMonitorServiceTest(String name){ super(name);}

  /*
   * Test method for 'org.sapia.soto.ubik.monitor.MonitorService.getStatusForClass(String)'
   */
  public void testGetStatusForClass() throws Exception{
    TestMonitoredService service = new TestMonitoredService(false);
    ServiceMetaData meta = new ServiceMetaData(new SotoContainer(), "test", service);
    TestMonitorAgentLayer layer = new TestMonitorAgentLayer();
    layer.setInvoke("perform");
    layer.init(meta);
    TestDomainMonitorService monitor = new TestDomainMonitorService();
    monitor.addAgent(layer);
    StatusResultList resultList = monitor.getStatusForClass(service.getClass().getName());
    super.assertEquals(1, resultList.getResults().size());
    StatusResult res = (StatusResult)resultList.getResults().get(0);
    super.assertTrue(!res.isError());
  }
  
  public void testGetStatusErrorForClass() throws Exception{
    TestMonitoredService service = new TestMonitoredService(true);
    ServiceMetaData meta = new ServiceMetaData(new SotoContainer(), "test", service);
    TestMonitorAgentLayer layer = new TestMonitorAgentLayer();
    layer.setInvoke("perform");
    layer.init(meta);
    TestDomainMonitorService monitor = new TestDomainMonitorService();
    monitor.addAgent(layer);
    StatusResultList resultList = monitor.getStatusForClass(service.getClass().getName());
    super.assertEquals(1, resultList.getResults().size());
    StatusResult res = (StatusResult)resultList.getResults().get(0);
    super.assertTrue(res.isError());
  
  }
  
  /*
   * Test method for 'org.sapia.soto.ubik.monitor.MonitorService.getStatusForId(String)'
   */
  public void testGetStatusForId() throws Exception{
    TestMonitoredService service = new TestMonitoredService(false);
    ServiceMetaData meta = new ServiceMetaData(new SotoContainer(), "test", service);
    TestMonitorAgentLayer layer = new TestMonitorAgentLayer();
    layer.setInvoke("perform");
    layer.init(meta);
    TestDomainMonitorService monitor = new TestDomainMonitorService();
    monitor.addAgent(layer);
    StatusResultList resultList = monitor.getStatusForId("test");
    super.assertEquals(1, resultList.getResults().size());
    StatusResult res = (StatusResult)resultList.getResults().get(0);
    super.assertTrue(!res.isError());
  }
  
  public void testGetStatusErrorForId() throws Exception{
    TestMonitoredService service = new TestMonitoredService(true);
    ServiceMetaData meta = new ServiceMetaData(new SotoContainer(), "test", service);
    TestMonitorAgentLayer layer = new TestMonitorAgentLayer();
    layer.setInvoke("perform");
    layer.init(meta);
    TestDomainMonitorService monitor = new TestDomainMonitorService();
    monitor.addAgent(layer);
    StatusResultList resultList = monitor.getStatusForId("test");
    super.assertEquals(1, resultList.getResults().size());
    StatusResult res = (StatusResult)resultList.getResults().get(0);
    super.assertTrue(res.isError());
  }  

  public void testLoadConfig() throws Exception{
    SotoContainer cont = new SotoContainer();
    cont.load(new File("etc/ubik/monitor.client.xml"));
    cont.start();
    
  }
}
