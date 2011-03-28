package org.sapia.soto.ubik.monitor.impl;

import org.sapia.soto.ubik.monitor.StatusResultList;
import org.sapia.soto.ubik.monitor.impl.DomainMonitorService;

public class TestDomainMonitorService extends DomainMonitorService{
  
  boolean wasCalled;

  public StatusResultList getStatusForClass(String className) {
    wasCalled = true;
    return super.getStatusForClass(className);
  }
  
  public StatusResultList getStatusForId(String serviceId) {
    wasCalled = true;
    return super.getStatusForId(serviceId);
  }
  protected void lookupChannel() throws Exception {
  }
  
}
