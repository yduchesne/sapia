
package org.sapia.soto.ubik;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sapia.soto.Debug;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.NotFoundException;
import org.sapia.soto.Service;
import org.sapia.ubik.rmi.naming.remote.discovery.ServiceDiscoListener;
import org.sapia.ubik.rmi.naming.remote.discovery.ServiceDiscoveryEvent;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 * This service is used has a proxy that attempts to lookup remote objects
 * from remote naming services. An instance of this instances creates
 * a dynamic proxy (through Java reflection). It should be configured with
 * the name of the interfaces that it should implement.
 *
 * @see NamingService
 *
 * @author yduchesne
 */
public class ProxyService implements ObjectCreationCallback, 
        Service, ServiceDiscoListener, EnvAware{
  
  private List _interfaces = new ArrayList();
  private Env _env;
  private NamingService _naming;
  private String _name;
  private Object _remote;
  private static Set _serviceIfMethods = new HashSet();
  static{
    _serviceIfMethods.add("init");
    _serviceIfMethods.add("start");
    _serviceIfMethods.add("dispose");
  }
  
  /**
   * Creates a new instance of ProxyService.
   * 
   */
  public ProxyService() {
    _interfaces.add(Service.class);
  }
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public void addImplements(String intf) throws Exception {
    _interfaces.add(Class.forName(intf));
  }
  
  public void addImplements(Class anInterface) {
    _interfaces.add(anInterface);
  }
  
  public void setJndiName(String name){
    _name = name;
  }
  
  public void setNamingService(NamingService naming){
    _naming = naming;
  }
  
  public Object onCreate() throws ConfigurationException{
    if(_name == null){
      throw new ConfigurationException("JNDI name not set on proxy service");
    }
    if(_naming == null){
      try{
        _naming = (NamingService)_env.lookup(NamingService.class);
      }catch(NotFoundException e){
        throw new ConfigurationException("Could not find naming service", e);
      }
    }
    
    try{
      _remote = _naming.lookup(_name);
      Debug.debug(getClass(), "Resolved remote service: " + _remote);
    }catch(Exception e){
      Debug.debug(getClass(), "Could not resolve remote service: " + _remote + "; attempting discovery");
      _naming.register(this);
    }
    
    Class[] interfaces = new Class[_interfaces.size()];
    
    for(int i = 0; i < _interfaces.size(); i++){
      interfaces[i] = (Class)_interfaces.get(i);
    }
    
    return Proxy.newProxyInstance(
      Thread.currentThread().getContextClassLoader(),
      interfaces,
      new ProxyInvocationHandler(this)
    );
    
  }

  public void init() throws Exception {
  }

  public void start() throws Exception {
  }

  public void dispose() {
  }
  
  /**
   * Callback method after an invocation on the remote object occured.
   * 
   * @param aMethod The method called.
   * @param someArgs The arguments passed.
   * @param aResult The result of the invocation.
   * @return The objet to return by this proxy on the method invocation.
   */
  public Object onPostInvokeEvent(Method aMethod, Object[] someArgs, Object aResult) {
    return aResult;
  }

  /* (non-Javadoc)
   * @see org.sapia.ubik.rmi.naming.remote.discovery.ServiceDiscoListener#onServiceDiscovered(org.sapia.ubik.rmi.naming.remote.discovery.ServiceDiscoveryEvent)
   */
  public void onServiceDiscovered(ServiceDiscoveryEvent anEvent) {
    // Matching service name as-is or without the initial '/' character
    if (anEvent.getName().equals(_name) || 
        (anEvent.getName().charAt(0) == '/' && anEvent.getName().substring(1).equals(_name))) {
      try{
        _remote = anEvent.getService();
        _naming.unregister(this);
        
      } catch (Exception e) {
        Debug.debug(getClass(), "Could not resolve remote service: " + _name);
        Debug.debug(getClass(), e);
      }
    }
  }
  
  /**
   * Internal proxy class to handle invocations
   * 
   */
  public class ProxyInvocationHandler implements InvocationHandler {
    private ProxyService _owner;
    ProxyInvocationHandler(ProxyService owner){
      _owner = owner;
    }
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      // Forward Service life-cycle calls to the proxy service 
      if (method.getParameterTypes().length == 0 && method.getReturnType() == void.class &&
          (_serviceIfMethods.contains(method.getName()))){
        try{
          return method.invoke(_owner, args);
        }catch(InvocationTargetException e){
          throw e.getTargetException();
        }
        
      // Otherwise assume it is a remote call
      } else if (_owner._remote != null) {
        try{
          Object result = method.invoke(_owner._remote, args);
          return _owner.onPostInvokeEvent(method, args, result);          
        }catch(InvocationTargetException e){
          throw e.getTargetException();
        }
      } else {
        try{
          // attempting calling on this (in case of toString(), equals()...)
          return method.invoke(this, args);
        } catch (InvocationTargetException e){
          throw e.getTargetException();
        } catch (Exception e) {
        }
       
        throw new UnavailableRemoteObjectException(_owner._name);
      }
    }
  }

}
