package org.sapia.soto.ubik.monitor.impl;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Properties;

import org.sapia.corus.interop.Context;
import org.sapia.corus.interop.Param;
import org.sapia.corus.interop.Status;
import org.sapia.corus.interop.api.InteropLink;
import org.sapia.corus.interop.api.StatusRequestListener;
import org.sapia.soto.ubik.monitor.MonitorAgent;

public class MonitorStatusRequestListener {
  
  public static final String SOTO_MONITOR_ERROR       = "soto.monitor.error";
  public static final String SOTO_MONITOR_ERROR_CLASS = "soto.monitor.error.class";  
  public static final String SOTO_MONITOR_ERROR_MSG   = "soto.monitor.error.msg";  
  
  private CorusListener _listener;
  
  public MonitorStatusRequestListener(MonitorAgent agent){
    _listener = new CorusListener(agent);
    InteropLink.getImpl().addStatusRequestListener(_listener);    
  }
  
  /////// Corus IOP StatusRequestListener impl
  
  public static class CorusListener implements StatusRequestListener{
  
    MonitorAgent _agent;
    
    public CorusListener(MonitorAgent agent){
      _agent = agent;
    }
    
    public void onStatus(Status status) {
      org.sapia.soto.ubik.monitor.Status agentStatus = null;
      
      try{
        agentStatus = _agent.checkStatus();
      }catch(RemoteException e){
        return;
      }
      
      Context ctx = new Context();
      Param errorFlag = new Param();
      errorFlag.setName(SOTO_MONITOR_ERROR);
      if(agentStatus.isError()){
        errorFlag.setValue("true");
      }
      else{
        errorFlag.setValue("false");
      }
      ctx.addParam(errorFlag);
      if(agentStatus.getError() != null){
        ctx.addParam(new Param(SOTO_MONITOR_ERROR_CLASS, 
            agentStatus.getError().getClass().getName()));
        if(agentStatus.getError().getMessage() != null){
          ctx.addParam(new Param(SOTO_MONITOR_ERROR_MSG, 
              agentStatus.getError().getMessage()));
        }
      }

      if(agentStatus.getServiceId() != null){
        ctx.setName(agentStatus.getServiceId());
      }
      else{
        ctx.setName(agentStatus.getServiceClassName());
      }
      copyProperties(ctx, agentStatus.getResultProperties());
      status.addContext(ctx);
    }
    
    private void copyProperties(Context ctx, Properties props){
      Enumeration names = props.propertyNames();
      while(names.hasMoreElements()){
        String name = (String)names.nextElement();
        String value = props.getProperty(name);
        if(value != null){
          Param p = new Param();
          p.setName(name);
          p.setValue(value);
          ctx.addParam(p);
        }
      }
    }
  }

}
