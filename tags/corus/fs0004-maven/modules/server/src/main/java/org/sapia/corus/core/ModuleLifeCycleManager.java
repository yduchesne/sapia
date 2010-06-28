package org.sapia.corus.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.log.Hierarchy;
import org.apache.log.Logger;
import org.sapia.corus.client.annotations.Bind;
import org.sapia.corus.core.property.PropertyContainer;
import org.sapia.corus.core.property.PropertyProvider;
import org.sapia.ubik.net.TCPAddress;
import org.springframework.beans.FatalBeanException;
import org.springframework.context.ApplicationContext;

/**
 * An instance of this class supplements the Spring bean lifecyle by supporting additional constructs:
 * 
 * <ul>
 *   <li>It recognizes the {@link Service} interface.
 *   <li>It recognizes the {@link Bind} annotation.
 * </ul>
 * 
 * <p>
 * Instances that are annotated with {@link Bind} are automatically bound to this instance's {@link InternalServiceContext}.
 * 
 * @author yduchesne
 *
 */
class ModuleLifeCycleManager implements ServerContext, PropertyProvider{
  
  private Logger logger = Hierarchy.getDefaultHierarchy().getLoggerFor(getClass().getSimpleName());
  private List<ApplicationContext> contexts = new ArrayList<ApplicationContext>();
  private ServerContextImpl delegate;
  private PropertyContainer properties;

  ModuleLifeCycleManager(ServerContextImpl serverContext, PropertyContainer properties) {
    this.delegate = serverContext;
    this.properties = properties;
  }
  
  ///////// ServerContext interface

  @Override
  public String getDomain() {
    return delegate.getDomain();
  }
  
  @Override
  public String getHomeDir() {
    return delegate.getHomeDir();
  }
  
  @Override
  public TCPAddress getServerAddress() {
    return delegate.getServerAddress();
  }
  
  @Override
  public String getServerName() {
    return delegate.getServerName();
  }
  
  @Override
  public void overrideServerName(String serverName) {
    delegate.overrideServerName(serverName);
  }
  
  @Override
  public InternalServiceContext getServices() {
    return delegate.getServices();
  }
 
  @Override
  public <S> S lookup(Class<S> serviceInterface) {
    return delegate.lookup(serviceInterface);
  }
  
  ///////// PropertyProvider interface
  
  @Override
  public void overrideInitProperties(PropertyContainer properties) {
    this.properties = properties;
  }
  
  @Override
  public PropertyContainer getInitProperties() {
    return properties;
  }
  
  ///////// Instance methods
  
  ServerContext getServerContext(){
    return delegate;
  }
  
  void setServerAddress(TCPAddress addr){
    delegate.setServerAddress(addr);
  }
  
  void addApplicationContext(ApplicationContext ctx){
    contexts.add(ctx);
  }

  Object lookup(String name){
    for(ApplicationContext ctx:contexts){
      Object bean = ctx.getBean(name);
      if(bean != null){
        return bean;
      }
    }
    return null;
  }
  
  void startServices() throws Exception{
    
    for(ApplicationContext context:contexts){
      for(String name:context.getBeanDefinitionNames()){
        Object bean = context.getBean(name);
        if(bean != null){
          if(bean.getClass().isAnnotationPresent(Bind.class)){
            Bind bind = bean.getClass().getAnnotation(Bind.class);
            logger.debug(String.format("Binding %s as module %s", bean.getClass().getName(), bind.moduleInterface().getName()));
            delegate.getServices().bind(bind.moduleInterface(), bean);
          }
        }
        if(bean instanceof Service){
          try{
            Service service = (Service)bean;
            service.start();
          }catch(Exception e){
            throw new FatalBeanException("Error performing service initialization for " + name, e);
          }
        }
      }
    }
  }  
}
