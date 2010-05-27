package org.sapia.corus;

import org.apache.log.Hierarchy;
import org.apache.log.Logger;
import org.sapia.corus.admin.Module;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Yanick Duchesne
 */
public abstract class ModuleHelper implements ApplicationContextAware, Service, Module{
  
  protected Logger  _log = Hierarchy.getDefaultHierarchy().getLoggerFor(getClass().getName());
  protected ApplicationContext     _appContext;
  protected ServerContext          _serverContext;

  public ModuleHelper() {
    super();
  }

  @Override
  public void setApplicationContext(ApplicationContext appCtx)
      throws BeansException {
    _appContext = appCtx;
    _serverContext = InitContext.get().getServerContext();
    preInit();
  }
  
  public void preInit(){}
  
  public void start() throws Exception {}

  public Logger logger(){
    return _log;  
  }

  public ApplicationContext env(){
    return _appContext;
  }
  
  protected ServerContext serverContext(){
    return _serverContext;
  }
  
  protected InternalServiceContext services(){
    return _serverContext.getServices();
  }
  
  protected <S> S lookup(Class<S> serviceInterface){
    return _serverContext.lookup(serviceInterface);
  }

}
