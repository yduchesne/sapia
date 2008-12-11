package org.sapia.ubik.ioc.guice;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.NoSuchObjectException;

import org.sapia.ubik.rmi.naming.remote.discovery.ServiceDiscoListener;
import org.sapia.ubik.rmi.naming.remote.discovery.ServiceDiscoveryEvent;
import org.sapia.ubik.rmi.server.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * This provider is used in order to lookup remote objects from remote Ubik JNDI servers. 
 * <p>
 * This provider supports lazy lookups of services: it allows for Ubik services to appear on
 * the network in an ad-hoc manner. More precisely, given services must not necessarily appear on the network
 * before the applications that depend on them.
 * <p>
 * This allows client applications to startup without risking abrupt termination because of failed service lookups:
 * if a given service is not found, a lazy proxy is created and returned at its place. A discovery mechanism 
 * insures that the missing service is eventually retrieved (if it appears on the network). 
 * <p>
 * Example:
 * <pre>
 * // Bind to DataSource from JNDI.
 * bind(MyService.class).toProvider(new RemoteServiceProvider&lt;MyService&gt;(MyService.class, "my/service"));
 * </pre>
 * Note that a {@link NamingService} must have been bound beforehand (so that it can be injected into 
 * this instance).
 * 
 * @see NamingService
 * 
 * @author yduchesne
 */
public class RemoteServiceProvider<T> implements Provider<T>{

  @Inject
  private NamingService _naming;

  private String _name;

  private T _remote;

  private Class<T> _type;

  /**
   * Creates a new instance of {@link RemoteServiceProvider}.
   * 
   * @param type the {@link Class} of the service to retrieve.
   * @param jndiName the name under which the expected service is bound into
   * Ubik's JNDI server(s).
   */
  public RemoteServiceProvider(Class<T> type, String jndiName) {
    _type = type;
    _name = jndiName;
  }

  public T get() {
    if (_name == null) {
      throw new IllegalStateException("JNDI name not set on this provider");
    }
    if (_naming == null) {
      throw new IllegalStateException(NamingService.class.getName()
          + " instance not set on this provider");
    }

    try {
      _remote = _type.cast(_naming.lookup(_name));
      if (Log.isInfo()) {
        Log.info(getClass(), "Resolved remote service: " + _name);
      }
      
      // returning the remote object that was found
      return _remote;
    } catch (Exception e) {
      Log.warning(getClass(), "Could not resolve remote service: " + _name
          + "; attempting discovery");
      _naming.register(new ServiceDiscoListenerImpl());
      
      // not found remote object, returning lazy proxy
      return _type.cast(Proxy.newProxyInstance(Thread.currentThread()
          .getContextClassLoader(), new Class[] { _type },
          new ProxyInvocationHandler(this)));
    }
  }

  /**
   * Callback method after an invocation on the remote object occured.
   * 
   * @param aMethod
   *          The method called.
   * @param someArgs
   *          The arguments passed.
   * @param aResult
   *          The result of the invocation.
   * @return The objet to return by this proxy on the method invocation.
   */
  public Object onPostInvokeEvent(Method aMethod, Object[] someArgs,
      Object aResult) {
    return aResult;
  }
  
  
  ////////////////// INNER CLASSES ///////////////////
  
  class ServiceDiscoListenerImpl implements ServiceDiscoListener{ 

    public void onServiceDiscovered(ServiceDiscoveryEvent anEvent) {
      // Matching service name as-is or without the initial '/' character
      if (anEvent.getName().equals(_name)
          || (anEvent.getName().charAt(0) == '/' && anEvent.getName()
              .substring(1).equals(_name))) {
        try {
          _remote = _type.cast(anEvent.getService());
          _naming.unregister(this);
  
        } catch (Exception e) {
          Log.error(getClass(), "Could not resolve remote service: " + _name, e);
        }
      }
    }
  }

  /**
   * Internal proxy class to handle invocations
   */
  public class ProxyInvocationHandler implements InvocationHandler {

    private RemoteServiceProvider<T> _owner;

    ProxyInvocationHandler(RemoteServiceProvider<T> owner) {
      _owner = owner;
    }

    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable {
      // Forward Service life-cycle calls to the proxy service
      if (_owner._remote != null) {
        try {
          Object result = method.invoke(_owner._remote, args);
          return _owner.onPostInvokeEvent(method, args, result);
        } catch (InvocationTargetException e) {
          throw e.getTargetException();
        }
      } else {
        try {
          // attempting calling on this (in case of toString(), equals()...)
          return method.invoke(this, args);
        } catch (InvocationTargetException e) {
          throw e.getTargetException();
        } catch (Exception e) {
        }

        throw new NoSuchObjectException(_owner._name);
      }
    }
  }

}