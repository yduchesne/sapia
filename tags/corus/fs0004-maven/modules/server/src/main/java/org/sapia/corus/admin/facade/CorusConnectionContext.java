package org.sapia.corus.admin.facade;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.sapia.corus.admin.Corus;
import org.sapia.corus.admin.Result;
import org.sapia.corus.admin.Results;
import org.sapia.corus.admin.exceptions.CorusException;
import org.sapia.corus.admin.exceptions.cli.ConnectionException;
import org.sapia.corus.admin.services.cluster.ClusterManager;
import org.sapia.corus.cluster.ClusterInterceptor;
import org.sapia.corus.core.ClusterInfo;
import org.sapia.ubik.net.ServerAddress;
import org.sapia.ubik.net.TCPAddress;
import org.sapia.ubik.rmi.server.Hub;

public class CorusConnectionContext {

  static final long RECONNECT_INTERVAL = 15000;
  protected long _lastReconnect = System.currentTimeMillis();
  protected Corus _corus;
  protected ServerAddress _addr;
  protected String _domain;
  protected Map<Class<?>, Object> _modules = new HashMap<Class<?>, Object>();
  protected ClusterInterceptor _interceptor;
  protected Set<ServerAddress> _otherHosts = new HashSet<ServerAddress>();
  protected Map<ServerAddress, Corus> _cachedStubs = Collections
      .synchronizedMap(new HashMap<ServerAddress, Corus>());

  public CorusConnectionContext(String host, int port) throws Exception {
    _interceptor = new ClusterInterceptor();
    reconnect(host, port);
  }

  /**
   * @return the Corus server's version.
   */
  public String getVersion(){
    refresh();
    return _corus.getVersion();
  }

  /**
   * @return the domain/cluster of the Corus server to which this instance is
   *         connected.
   */
  public String getDomain() {
    return _domain;
  }

  /**
   * @return the {@link ServerAddress} of the other Corus instances in the
   *         cluster.
   */
  public Collection<ServerAddress> getOtherAddresses() {
    refresh();
    return Collections.unmodifiableCollection(_otherHosts);
  }

  /**
   * @return the remote {@link Corus} instance, corresponding to the server's
   *         interface.
   */
  public Corus getCorus() {
    refresh();
    return _corus;
  }

  /**
   * @return the {@link ServerAddress} of the Corus server to which this
   *         instance is connected.
   */
  public ServerAddress getAddress() {
    return _addr;
  }

  /**
   * Reconnects to the corus server at the given host/port.
   * 
   * @param host
   *          the host of the server to reconnect to.
   * @param port
   *          the port of the server to reconnect to.
   * @throws CorusException
   */
  public synchronized void reconnect(String host, int port) {
    _addr = new TCPAddress(host, port);
    reconnect();
  }

  /**
   * Reconnects to the corus server that this instance corresponds to.
   * 
   * @throws CorusException
   */
  public synchronized void reconnect() {
    try {
      _corus = (Corus) Hub.connect(((TCPAddress) _addr).getHost(),
          ((TCPAddress) _addr).getPort());
      _domain = _corus.getDomain();
      _otherHosts.clear();
      _cachedStubs.clear();
      _modules.clear();

      ClusterManager mgr = (ClusterManager) _corus.lookup(ClusterManager.ROLE);
      _otherHosts.addAll(mgr.getHostAddresses());
    } catch (RemoteException e) {
      throw new ConnectionException("Could not reconnect to Corus server", e);
    }
  }

  public void clusterCurrentThread(ClusterInfo info) {
    refresh();
    ClusterInterceptor.clusterCurrentThread(info);
  }

  @SuppressWarnings(value = "unchecked")
  <T,M> void invoke(Results<T> results, Class<M> moduleInterface, String methodName,
      Object[] params, Class[] sig, ClusterInfo cluster) throws Throwable {
    refresh();

    try {
      Object returnValue = moduleInterface.getMethod(methodName, sig).invoke(lookup(moduleInterface), params);
      results.addResult(new Result(_addr, returnValue));
    } catch (InvocationTargetException e) {
      //results.addResult(new Result(_addr, e.getTargetException()));
      throw e.getTargetException();
    }

    if (cluster.isClustered()) {
      ClusterInterceptor.clusterCurrentThread(cluster);
      applyToCluster(results, moduleInterface, methodName, params, sig, cluster);
    }
  }
  
  <T,M> T invoke(Class<T> returnType, Class<M> moduleInterface, Method method, Object[] args, ClusterInfo info) throws Throwable{
    try{
      
      if(info.isClustered()){
        ClusterInterceptor.clusterCurrentThread(info);
      }
      
      Object toReturn = method.invoke(lookup(moduleInterface), args);
      if(toReturn != null){
        return returnType.cast(toReturn);
      }
      else{
        return null;
      }
    }catch(InvocationTargetException e){
      throw e.getTargetException();
    }
  }

  @SuppressWarnings(value = "unchecked")
  void applyToCluster(final Results results, final Class moduleInterface,
      final String methodName, final Object[] params, final Class[] sig,
      final ClusterInfo cluster) {
    Thread t = new Thread(new Runnable() {
      public void run() {
        Set<ServerAddress> otherHosts;

        if (cluster.getTargets() != null) {
          otherHosts = new HashSet<ServerAddress>(_otherHosts);
          otherHosts.retainAll(cluster.getTargets());
        } else {
          otherHosts = _otherHosts;
        }
        Iterator<ServerAddress> itr = otherHosts.iterator();
        TCPAddress addr;
        Corus corus;
        Object module;
        Object returnValue;

        while (itr.hasNext()) {
          addr = (TCPAddress) itr.next();
          corus = (Corus) _cachedStubs.get(addr);

          if (corus == null) {
            try {
              corus = (Corus) Hub.connect(addr.getHost(), addr.getPort());
              _cachedStubs.put(addr, corus);
            } catch (java.rmi.RemoteException e) {
              continue;
            }
          }

          try {
            module = corus.lookup(moduleInterface.getName());

            Method m = module.getClass().getMethod(methodName, sig);

            try {
              returnValue = m.invoke(module, params);
              results.addResult(new Result(addr, returnValue));
            } catch (InvocationTargetException ite) {
              results.addResult(new Result(addr, ite.getTargetException()));
            } catch (Exception e) {
              results.addResult(new Result(addr, e));
            }
            continue;
          } catch (Exception e) {
            e.printStackTrace();
          }
        }

        results.complete();
      }
    });
    t.setDaemon(true);
    t.start();
  }
  
  public synchronized <T> T lookup(Class<T> moduleInterface){
    Object module  = _modules.get(moduleInterface);
    if(module == null){
      module = _corus.lookup(moduleInterface.getName());
      _modules.put(moduleInterface, module);
    }
    return moduleInterface.cast(module);
  }

  protected void refresh() throws ConnectionException {
    if ((System.currentTimeMillis() - _lastReconnect) > RECONNECT_INTERVAL) {
      reconnect();
      _lastReconnect = System.currentTimeMillis();
    }
  }
}
