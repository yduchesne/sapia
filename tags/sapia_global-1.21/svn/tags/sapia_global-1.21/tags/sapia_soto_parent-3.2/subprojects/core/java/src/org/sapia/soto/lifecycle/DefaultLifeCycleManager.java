package org.sapia.soto.lifecycle;

import org.sapia.soto.Env;
import org.sapia.soto.Service;
import org.sapia.soto.ServiceConfiguration;

public class DefaultLifeCycleManager implements LifeCycleManager{
  
  public DefaultLifeCycleManager(){
  }
  
  public void init(Env env) {}
  public void postInit(Env env) {}
  public void dispose(Env env) {}
  
  public void initService(ServiceConfiguration conf) throws Exception {
    if(conf.getService() instanceof Service){
      ((Service) conf.getService()).init();
    }
    else{
      conf.invokeInitMethod();
    }
  }
  
  public Class getServiceClass(Object service) {
    return service.getClass();
  }
  
  public void startService(ServiceConfiguration conf) throws Exception {
    if(conf.getService() instanceof Service){
      ((Service) conf.getService()).start();
    }
    else{
      conf.invokeStartMethod();
    }
  }
  
  public Object lookupService(String name, Object service) {
    return service;
  }
  
  public void disposeService(ServiceConfiguration conf){
    if(conf.getService() instanceof Service){
      ((Service) conf.getService()).dispose();
    }
    else{
      conf.invokeDisposeMethod();
    }
  }  

}
