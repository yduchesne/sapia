package org.sapia.corus.core;

import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Properties;

import org.apache.log.Hierarchy;
import org.sapia.corus.admin.Corus;
import org.sapia.corus.admin.CorusVersion;
import org.sapia.corus.admin.services.naming.JndiModule;
import org.sapia.corus.core.spring.ConfigurationPostProcessor;
import org.sapia.corus.exceptions.CorusException;
import org.sapia.corus.util.CompositeStrLookup;
import org.sapia.corus.util.IOUtils;
import org.sapia.corus.util.PropertiesStrLookup;
import org.sapia.ubik.rmi.naming.remote.RemoteContext;
import org.sapia.ubik.rmi.naming.remote.RemoteContextProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * An instance of this class acts as a corus server's kernel.
 * It initializes the modules that are part of the server and
 * provides a method to lookup any given module.
 *
 * @author Yanick Duchesne
 */
public class CorusImpl implements Corus, RemoteContextProvider {
  private static ApplicationContext           _appContext;
  private  static ModuleLifeCycleManager _lifeCycle;
  private static CorusImpl                    _instance;
  private String                              _domain;

  private CorusImpl(String domain) {
    _domain        = domain;
  }

  public String getVersion() {
    return CorusVersion.create().toString();
  }
  
  public String getDomain() {
    return _domain;
  }
  
  public static ServerContext init(Hierarchy h, InputStream config, String domain,
                          CorusTransport aTransport, String corusHome) throws java.io.IOException, Exception {
    _instance = new CorusImpl(domain);
    CorusRuntime.init(_instance, corusHome, aTransport);
    
    // loading default properties.
    final Properties props = new Properties();
    InputStream defaults = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/sapia/corus/default.properties");
    if(defaults == null){
      throw new IllegalStateException("Resource 'org/sapia/corus/default.properties' not found");
    }
    
    InputStream tmp = IOUtils.replaceVars(new PropertiesStrLookup(System.getProperties()), defaults);
    defaults.close();
    props.load(tmp);
    
    // loading user properties (from config/corus.properties).
    //Properties userProps = new Properties();
    CompositeStrLookup lookup = new CompositeStrLookup();
    lookup.add(new PropertiesStrLookup(props)).add(new PropertiesStrLookup(System.getProperties()));
    tmp = IOUtils.replaceVars(lookup, config);
    props.load(tmp);
    
    InternalServiceContext services = new InternalServiceContext();
    ServerContext serverContext = new ServerContext(domain, corusHome,  services);
    InitContext.attach(new PropertyContainer(){
      public String getProperty(String name) {
        return props.getProperty(name);
      }
    },
    serverContext);
    try{
      _appContext = new ClassPathXmlApplicationContext("org/sapia/corus/core.xml");
      _lifeCycle = (ModuleLifeCycleManager)_appContext.getBean(ModuleLifeCycleManager.class.getName());
      if(_lifeCycle == null){
        throw new IllegalStateException(ModuleLifeCycleManager.class.getName() + " not found");
      }
      ClassPathXmlApplicationContext moduleContext = new ClassPathXmlApplicationContext(_appContext);
      moduleContext.addBeanFactoryPostProcessor(new ConfigurationPostProcessor(_lifeCycle));
      moduleContext.setConfigLocation("org/sapia/corus/modules.xml");
      moduleContext.refresh();

    }finally{
      InitContext.unattach();
    }
    
    return serverContext;
  }
  
  public static void start() throws Exception {
    _lifeCycle.startServices();
  }
  
  public static void shutdown(){
    _lifeCycle.disposeServices();
  }
  
  public Object lookup(String module) throws CorusException {
    try {
      Object toReturn = _appContext.getBean(module);
      if(toReturn == null){
        throw new IllegalArgumentException(String.format("No module found for: %s", module));
      }
      return toReturn;
    } catch (Exception e) {
      e.printStackTrace();
      throw new CorusException(e);
    }
  }
  
  public static CorusImpl getInstance() {
    if (_instance == null) {
      throw new IllegalStateException("corus not initialized");
    }

    return _instance;
  }
  
  public RemoteContext getRemoteContext() throws RemoteException{
    try{
      JndiModule module = (JndiModule)lookup(JndiModule.ROLE);
      return module.getRemoteContext();
    }catch(CorusException e){
      throw new RuntimeException(e);
    }
  }
}
