package org.sapia.corus;

import java.util.ArrayList;
import java.util.List;

import org.apache.log.Hierarchy;
import org.apache.log.Logger;
import org.sapia.corus.annotations.Bind;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class ModuleLifeCycleManager implements BeanPostProcessor{
  
  private List<Service> services = new ArrayList<Service>();
  private Logger logger = Hierarchy.getDefaultHierarchy().getLoggerFor(getClass().getSimpleName());
  
  ///////////////// BeanPostProcessor interface
  
  @Override
  public Object postProcessAfterInitialization(Object module, String name)
      throws BeansException {
    
    if(module.getClass().isAnnotationPresent(Bind.class)){
      Bind bind = module.getClass().getAnnotation(Bind.class);
      logger.debug(String.format("Binding %s as module %s", module.getClass().getName(), bind.moduleInterface().getName()));
      InitContext.get().getServerContext().getServices().bind(bind.moduleInterface(), module);
    }
    
    if(module instanceof Service){
      try{
        Service service = (Service)module;
        service.init();
        services.add(service);
        return module;
      }catch(Exception e){
        throw new FatalBeanException("Error performing service initialization for " + name, e);
      }
    }
    else{
      return module;
    }
  }
  
  @Override
  public Object postProcessBeforeInitialization(Object module, String name)
      throws BeansException {
    return module;
  }
  
  ///////////////// Instance methods
  
  public void startServices() throws Exception{
    for(Service s:services){
      s.start();
    }
  }
  
  public void disposeServices(){
    for(int i = services.size() - 1; i >= 0; i--){
      Service s = services.get(i);
      try{
        s.dispose();
      }catch(Exception e){
        logger.error(String.format("Error disposing service: %s", s.getClass().getName()), e);
      }
    }
  }

}
