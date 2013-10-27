package org.sapia.corus.core;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Properties;

import org.sapia.corus.client.Corus;
import org.sapia.corus.client.CorusVersion;
import org.sapia.corus.client.exceptions.core.ServiceNotFoundException;
import org.sapia.corus.client.services.cluster.CorusHost;
import org.sapia.corus.client.services.naming.JndiModule;
import org.sapia.ubik.mcast.EventChannel;
import org.sapia.ubik.net.ServerAddress;
import org.sapia.ubik.rmi.naming.remote.RemoteContext;
import org.sapia.ubik.rmi.naming.remote.RemoteContextProvider;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;


/**
 * An instance of this class acts as a Corus server's kernel.
 * It initializes the modules that are part of the server and
 * provides a method to lookup any given module.
 *
 * @author Yanick Duchesne
 */
public class CorusImpl implements Corus, RemoteContextProvider {
  
  private ModuleLifeCycleManager      lifeCycle;
  private String                      domain;

  CorusImpl(
      Properties config, 
      String domain,
      ServerAddress serverAddress,
      EventChannel channel,
      CorusTransport aTransport, 
      String corusHome) throws IOException, Exception{
    init(config, domain, serverAddress, channel, aTransport, corusHome);
  }

  public String getVersion() {
    return CorusVersion.create().toString();
  }
  
  public String getDomain() {
    return domain;
  }
  
  public RemoteContext getRemoteContext() throws RemoteException{
    JndiModule module = (JndiModule)lookup(JndiModule.ROLE);
    return module.getRemoteContext();
  }

  public CorusHost getHostInfo() {
    return lifeCycle.getCorusHost();
  }

  public ServerContext getServerContext(){
    return lifeCycle;
  }
  
  private ServerContext init(
                          final Properties props, 
                          String domain,
                          ServerAddress address,
                          EventChannel channel,
                          CorusTransport aTransport, 
                          String corusHome) throws IOException, Exception {
    this.domain = domain;
    
    InternalServiceContext services = new InternalServiceContext();
    ServerContextImpl serverContext = new ServerContextImpl(this, aTransport, address, channel, domain, corusHome, services, props);
    
    // root context
    PropertyContainer propContainer = new PropertyContainer() {
      @Override
      public String getProperty(String name) {
        return props.getProperty(name);
      }
    };
    
    final ModuleLifeCycleManager manager         = new ModuleLifeCycleManager(serverContext, propContainer);
    BeanFactoryPostProcessor configPostProcessor = new ConfigurationPostProcessor(manager);
    
    GenericApplicationContext rootContext = new GenericApplicationContext();
    rootContext.getBeanFactory().registerSingleton("lifecycleManager", manager);
    rootContext.refresh();
    
    // core services context
    ClassPathXmlApplicationContext coreContext = new ClassPathXmlApplicationContext(rootContext);
    coreContext.addBeanFactoryPostProcessor(configPostProcessor);
    coreContext.registerShutdownHook();
    coreContext.setConfigLocation("org/sapia/corus/core.xml");
    coreContext.refresh();
    manager.addApplicationContext(coreContext);
    
    // module context
    ClassPathXmlApplicationContext moduleContext = new ClassPathXmlApplicationContext(coreContext);
    moduleContext.addBeanFactoryPostProcessor(configPostProcessor);
    moduleContext.registerShutdownHook();
    moduleContext.setConfigLocation("org/sapia/corus/modules.xml");
    moduleContext.refresh();
    manager.addApplicationContext(moduleContext);

    lifeCycle = manager;
 
    return serverContext;
  }
  
  public void start() throws Exception {
    lifeCycle.startServices();
  }
  
  public Object lookup(String module) throws ServiceNotFoundException{
    Object toReturn = lifeCycle.lookup(module);
    if(toReturn == null){
      throw new ServiceNotFoundException(String.format("No module found for: %s", module));
    }
    return toReturn;
  }
}
