package org.sapia.soto.ubik.monitor.mock;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.sapia.soto.Service;
import org.sapia.soto.ubik.monitor.MonitorAgent;
import org.sapia.soto.ubik.monitor.Status;
import org.sapia.soto.util.Param;

public class MockMonitorAgent implements MonitorAgent, Service{
  
  private boolean err;
  private String id, className;
  private List params = new ArrayList();
  
  public void setError(boolean err){
    this.err = err;
  }
  
  public void setId(String id){
    this.id = id;
  }
  
  public void setClassName(String className){
    this.className = className;
  }  
  
  public Status checkStatus() throws RemoteException {
    Status stat = new Status(className, id);
    System.out.println("STATUS !!!!!!!");
    if(err){
      Exception e = new Exception("Got error");
      e.fillInStackTrace();
      stat.setError(e);
    }
    stat.addResultProperties(System.getProperties());
    return stat;
  }
  
  public Param createParam(){
    Param p = new Param();
    params.add(p);
    return p;
  }
  
  public void init() throws Exception {
    if(className == null && id == null){
      throw new IllegalStateException("Status id or class name not set");
    }
  }
  
  public void start() throws Exception {
  }
  
  public void dispose() {
  }
  
  

}
