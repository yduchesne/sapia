package org.sapia.soto.ubik.monitor.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.sapia.soto.Debug;
import org.sapia.soto.Service;
import org.sapia.soto.ubik.monitor.Monitor;
import org.sapia.soto.ubik.monitor.MonitorAgent;
import org.sapia.soto.ubik.monitor.StatusResultList;
import org.sapia.soto.ubik.monitor.impl.AbstractMonitor.AgentBinding;
import org.sapia.soto.util.Utils;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;

/**
 * This class implements a <code>Monitor</code> whose instance are composed of other <code>Monitors</code> 
 * and can also encapsulates <code>MonitorAgents</code>.
 * 
 * @author Yanick Duchesne
 *
 */
public class CompositeMonitorService extends AbstractMonitor
               implements Service, ObjectHandlerIF{

  private List _monitors = Collections.synchronizedList(new ArrayList());

  public StatusResultList getStatusForClass(String className) {
    StatusResultList resultList = new StatusResultList();
    super.doGetStatus(className, resultList, false);
    doGetMonitorStatus(className, resultList, false);
    Collections.sort(resultList.getModifiableResults());    
    return resultList;
  }

  public StatusResultList getStatusForId(String serviceId) {
    StatusResultList resultList = new StatusResultList();    
    super.doGetStatus(serviceId, resultList, true);
    doGetMonitorStatus(serviceId, resultList, true);
    Collections.sort(resultList.getModifiableResults());    
    return resultList;
  } 
  
  /**
   * @param mon a <code>Monitor</code>
   */
  public void addMonitor(Monitor mon){
    _monitors.add(mon);
  }

  public void handleObject(String anElementName, Object anObject) throws ConfigurationException {
    if(anObject instanceof MonitorAgent){
      super.addAgent((MonitorAgent)anObject);
    }
    else if(anObject instanceof Monitor){
      if(Debug.DEBUG){
        Debug.debug(getClass(), "Adding monitor to this instance: " + anObject);
      }
      _monitors.add(anObject);
    }
    else{
      throw new ConfigurationException(Utils.createInvalidAssignErrorMsg(
          anElementName, 
          anObject, 
          MonitorAgent.class));
    }
  }
  
  public void init() throws Exception {
  }
  
  public void dispose() {
  }
  
  public void start() throws Exception {
  }
  
  protected void doGetMonitorStatus(String idOrClass, 
      StatusResultList resultList, boolean isId) {
    synchronized(_monitors){
      if(Debug.DEBUG){
        Debug.debug(getClass(), "Number of monitors:" + _monitors.size());
      }
      for(int i = 0; i < _monitors.size(); i++){
        Monitor mon = (Monitor)_monitors.get(i);
        
        if(Debug.DEBUG){
          Debug.debug(getClass(), "Got monitor:" + mon);
        }        
        if(isId){
          resultList.addResults(mon.getStatusForId(idOrClass).getResults());
        }
        else{
          resultList.addResults(mon.getStatusForClass(idOrClass).getResults());
        }
      }
    }
  }
}
