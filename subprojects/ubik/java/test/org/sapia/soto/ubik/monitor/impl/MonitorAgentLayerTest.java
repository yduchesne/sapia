package org.sapia.soto.ubik.monitor.impl;

import org.sapia.soto.ServiceMetaData;
import org.sapia.soto.SotoContainer;
import org.sapia.soto.ubik.monitor.Status;
import org.sapia.soto.ubik.monitor.TestMonitoredService;

import junit.framework.TestCase;

public class MonitorAgentLayerTest extends TestCase {
  
  public MonitorAgentLayerTest(String name){
    super(name);
  }

  public void testCheckStatusOk() throws Exception{
    TestMonitoredService service = new TestMonitoredService(false);
    ServiceMetaData meta = new ServiceMetaData(new SotoContainer(), "test", service);
    TestMonitorAgentLayer layer = new TestMonitorAgentLayer();
    layer.setInvoke("perform");
    layer.init(meta);
    Status stat = layer.checkStatus();
    super.assertTrue(!stat.isError());
  }
  
  public void testCheckStatusOkWithId() throws Exception{
    TestMonitoredService service = new TestMonitoredService(false);
    ServiceMetaData meta = new ServiceMetaData(new SotoContainer(), "test", service);
    TestMonitorAgentLayer layer = new TestMonitorAgentLayer();
    layer.setId("agent");
    layer.setInvoke("perform");
    layer.init(meta);
    Status stat = layer.checkStatus();
    super.assertEquals("agent", stat.getServiceId());
    super.assertTrue(!stat.isError());
  }  

  public void testCheckStatusError() throws Exception{
    TestMonitoredService service = new TestMonitoredService(true);
    ServiceMetaData meta = new ServiceMetaData(new SotoContainer(), "test", service);
    TestMonitorAgentLayer layer = new TestMonitorAgentLayer();
    layer.setInvoke("perform");
    layer.init(meta);
    Status stat = layer.checkStatus();
    super.assertTrue(stat.isError());
  }
}
