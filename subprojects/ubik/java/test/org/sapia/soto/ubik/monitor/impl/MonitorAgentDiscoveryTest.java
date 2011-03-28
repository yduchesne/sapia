package org.sapia.soto.ubik.monitor.impl;

import java.util.ArrayList;
import java.util.List;

import org.sapia.soto.ServiceMetaData;
import org.sapia.soto.SotoContainer;
import org.sapia.soto.ubik.monitor.TestMonitoredService;

import junit.framework.TestCase;

public class MonitorAgentDiscoveryTest extends TestCase {

  public MonitorAgentDiscoveryTest(String arg0) {
    super(arg0);
  }


  public void testDiscover() throws Exception{
    TestMonitoredService service = new TestMonitoredService(true);
    List layers = new ArrayList();
    layers.add(new TestMonitorAgentLayer());
    ServiceMetaData meta = new ServiceMetaData(new SotoContainer(), "test", service, layers);
    SotoContainer cont = new SotoContainer();
    cont.bind(meta);
    meta = new ServiceMetaData(new SotoContainer(), "test2", new TestMonitorAgentLayer());
    cont.bind(meta);
    cont.start();
    
    MonitorAgentDiscovery disco = new MonitorAgentDiscovery(cont.toEnv());
    List result = disco.discover();
    assertEquals(2, result.size());
  }

}
