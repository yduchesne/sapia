package org.sapia.soto.ubik.monitor.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sapia.corus.interop.Context;
import org.sapia.corus.interop.Param;
import org.sapia.corus.interop.Status;
import org.sapia.soto.ServiceMetaData;
import org.sapia.soto.SotoContainer;
import org.sapia.soto.ubik.monitor.TestFeedbackMonitorableService;
import org.sapia.soto.ubik.monitor.TestMonitoredService;
import org.sapia.soto.ubik.monitor.impl.MonitorStatusRequestListener;

import junit.framework.TestCase;

public class MonitorStatusRequestListenerTest extends TestCase {
  
  private MonitorStatusRequestListener.CorusListener corus;

  public MonitorStatusRequestListenerTest(String arg0) {
    super(arg0);
  }

  public void testOnErrorStatus() throws Exception{
    TestMonitoredService service = new TestMonitoredService(true);
    ServiceMetaData meta = new ServiceMetaData(new SotoContainer(), "test", service);
    TestMonitorAgentLayer layer = new TestMonitorAgentLayer();
    layer.init(meta);
    corus = new MonitorStatusRequestListener.CorusListener(layer);
    
    Status stat = new Status();
    corus.onStatus(stat);
    assertEquals(1, stat.getContexts().size());
    Context ctx = (Context)stat.getContexts().get(0);
    assertEquals("test", ctx.getName());
    List list = ctx.getParams();
    Map params = new HashMap();
    for(int i = 0; i < list.size(); i++){
      Param p = (Param)list.get(i);
      params.put(p.getName(), p.getValue());
    }
    assertEquals("true", params.get(MonitorStatusRequestListener.SOTO_MONITOR_ERROR));
    assertEquals(Exception.class.getName(), params.get(MonitorStatusRequestListener.SOTO_MONITOR_ERROR_CLASS));    
    assertTrue(((String)params.get(MonitorStatusRequestListener.SOTO_MONITOR_ERROR_MSG)).indexOf("Status error") > -1);    
  }
  
  public void testOnOkStatus() throws Exception{
    TestFeedbackMonitorableService service = new TestFeedbackMonitorableService();
    ServiceMetaData meta = new ServiceMetaData(new SotoContainer(), "test", service);
    TestMonitorAgentLayer layer = new TestMonitorAgentLayer();
    layer.init(meta);
    corus = new MonitorStatusRequestListener.CorusListener(layer);    
    
    Status stat = new Status();
    corus.onStatus(stat);
    assertEquals(1, stat.getContexts().size());
    Context ctx = (Context)stat.getContexts().get(0);
    assertEquals("test", ctx.getName());
    List list = ctx.getParams();
    Map params = new HashMap();
    for(int i = 0; i < list.size(); i++){
      Param p = (Param)list.get(i);
      params.put(p.getName(), p.getValue());
    }
    assertEquals("false", params.get(MonitorStatusRequestListener.SOTO_MONITOR_ERROR));
    assertTrue(params.get(MonitorStatusRequestListener.SOTO_MONITOR_ERROR_CLASS) == null);    
    assertEquals("testValue", params.get("testProp"));    
  }  
}
