package org.sapia.corus.http.rest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.sapia.corus.client.ClusterInfo;
import org.sapia.corus.client.Corus;
import org.sapia.corus.client.Result;
import org.sapia.corus.client.Results;
import org.sapia.corus.client.Results.ResultListener;
import org.sapia.corus.client.cli.ClientFileSystem;
import org.sapia.corus.client.common.Matcheable;
import org.sapia.corus.client.common.Matcheable.Pattern;
import org.sapia.corus.client.facade.CorusConnectionContext;
import org.sapia.corus.client.facade.impl.ClientSideClusterInterceptor;
import org.sapia.corus.client.services.cluster.ClusterManager;
import org.sapia.corus.client.services.cluster.CorusHost;
import org.sapia.ubik.net.ServerAddress;
import org.sapia.ubik.rmi.server.Hub;
import org.sapia.ubik.rmi.server.invocation.ClientPreInvokeEvent;
import org.sapia.ubik.util.Assertions;

/**
 * An instance of this class encapsulates objects pertaining to the connection to a Corus server.
 * 
 * @author yduchesne
 * 
 */
public class RestConnectionContext implements CorusConnectionContext {

  static final int INVOKER_THREADS = 10;

  private Corus                         corus;
  private CorusHost                     serverHost;
  private String                        domain;
  private Map<Class<?>, Object>         modules      = Collections.synchronizedMap(new HashMap<Class<?>, Object>());
  private Map<ServerAddress, Corus>     cachedStubs  = Collections.synchronizedMap(new HashMap<ServerAddress, Corus>());
  private ExecutorService               executor;
  private ClientSideClusterInterceptor  interceptor;
  private ClientFileSystem              fileSys;
  private Pattern                       resultFilter = Matcheable.AnyPattern.newInstance();

  /**
   * @param current
   *          the current {@link Corus} host.
   * @param fileSys
   *          the {@link ClientFileSystem}.
   * @param invokerThreads
   *          the number of threads to use when dispatching clustered method
   *          calls to targeted Corus instances.
   */
  public RestConnectionContext(Corus current, ClientFileSystem fileSys, int invokerThreads) {
    this.corus          = current;
    this.serverHost     = corus.getHostInfo();
    this.domain         = corus.getDomain();
    this.interceptor    = new ClientSideClusterInterceptor();
    this.fileSys        = fileSys;
    cachedStubs.put(current.getHostInfo().getEndpoint().getServerAddress(), corus);
    Hub.getModules().getClientRuntime().addInterceptor(ClientPreInvokeEvent.class, interceptor);
    executor = Executors.newFixedThreadPool(invokerThreads);
  }

  /**
   * Internally calls the {@link RestConnectionContext#RestConnectionContext(Corus, ClientFileSystem, int)}, passing
   * in the default number of invoker threads (see {@link #INVOKER_THREADS}).
   * 
   * @param current
   *          the current {@link Corus} host.
   * @param fileSys
   *          the {@link ClientFileSystem}.
   */
  public RestConnectionContext(Corus current, ClientFileSystem fileSys) {
    this(current, fileSys, INVOKER_THREADS);
  }
  
  // --------------------------------------------------------------------------
  // Basic stuff: domain, version, ClientFileSystem.

  @Override
  public String getDomain() {
    return domain;
  }
  
  @Override
  public String getVersion() {
    return corus.getVersion();
  }

  @Override
  public ClientFileSystem getFileSystem() {
    return fileSys;
  }

  @Override
  public Corus getCorus() {
    return corus;
  }

  @Override
  public CorusHost getServerHost() {
    return serverHost;
  }

  @Override
  public ServerAddress getAddress() {
    return serverHost.getEndpoint().getServerAddress();
  }  
  
  // --------------------------------------------------------------------------
  // Result filtering is not used for now
  
  @Override
  public void setResultFilter(Pattern pattern) {
    this.resultFilter = pattern;
  }
  
  @Override
  public Pattern getResultFilter() {
    return resultFilter;
  }
  
  @Override
  public void unsetResultFilter() {
    resultFilter = Matcheable.AnyPattern.newInstance();
  }
  
  // --------------------------------------------------------------------------
  // Core functionality

  @Override
  public Collection<CorusHost> getOtherHosts() {
    return ((ClusterManager) corus.lookup(ClusterManager.ROLE)).getHosts();
  }

  @Override
  public CorusHost resolve(ServerAddress addr) throws IllegalArgumentException {
    if (addr.equals(serverHost.getEndpoint().getServerAddress())) {
      return serverHost;
    }
    
    Collection<CorusHost> hosts = ((ClusterManager) corus.lookup(ClusterManager.ROLE)).getHosts();
    CorusHost toReturn = null;
    for (CorusHost h : hosts) {
      if (h.getEndpoint().getServerAddress().equals(addr)) {
        toReturn = h;
      }
    }
   
    Assertions.notNull(toReturn, "Could not resolve address: %s", addr);
    return toReturn;
  }

  /**
   * Empty implementation.
   */
  @Override
  public Stack<ServerAddress> getConnectionHistory() {
    return new Stack<ServerAddress>();
  }

  @Override
  public synchronized <T> T lookup(Class<T> moduleInterface) {
    Object module = modules.get(moduleInterface);
    if (module == null) {
      module = corus.lookup(moduleInterface.getName());
      modules.put(moduleInterface, module);
    }
    return moduleInterface.cast(module);
  }
  
  @Override
  public void disconnect() {
    executor.shutdownNow();
  }

  @Override
  public void clusterCurrentThread(ClusterInfo info) {
    ClientSideClusterInterceptor.clusterCurrentThread(info);
  }

  @Override
  @SuppressWarnings(value = "unchecked")
  public <T, M> void invoke(Results<T> results, Class<M> moduleInterface, Method method, Object[] params, ClusterInfo cluster) throws Throwable {
    final List<Result<T>> resultList = new ArrayList<Result<T>>();
    results.addListener(new ResultListener<T>() {
      @Override
      public void onResult(Result<T> result) {
        resultList.add(result);
      }
    });

    try {
      if (cluster.isClustered()) {
        applyToCluster(results, moduleInterface, method, params, cluster);
      } else {
        T returnValue = (T) method.invoke(lookup(moduleInterface), params);
        results.incrementInvocationCount();
        results.addResult(new Result<T>(serverHost, returnValue, Result.Type.forClass(method.getReturnType())));
      }
    } catch (InvocationTargetException e) {
      throw e.getTargetException();
    }
  }

  @Override
  public <T, M> T invoke(Class<T> returnType, Class<M> moduleInterface, Method method, Object[] params, ClusterInfo info) throws Throwable {
    try {

      ClientSideClusterInterceptor.clusterCurrentThread(info);

      Object toReturn = method.invoke(lookup(moduleInterface), params);
      if (toReturn != null) {
        return returnType.cast(toReturn);
      } else {
        return null;
      }
    } catch (InvocationTargetException e) {
      throw e.getTargetException();
    } finally {
      ClientSideClusterInterceptor.unregister();
    }
  }

  @SuppressWarnings(value = "unchecked")
  void applyToCluster(final Results<?> results, final Class<?> moduleInterface, final Method method, final Object[] params, final ClusterInfo cluster) {

    List<CorusHost> hostList = new ArrayList<CorusHost>();
    if (cluster.isTargetingAllHosts()) {
      hostList.add(serverHost);
      for (CorusHost otherHost : getOtherHosts()) {
        hostList.add(otherHost);
      }
    } else {
      for (ServerAddress t : cluster.getTargets()) {
        if (serverHost.getEndpoint().getServerAddress().equals(t)) {
          hostList.add(serverHost);
          break;
        } else {
          for (CorusHost o : getOtherHosts()) {
            if (o.getEndpoint().getServerAddress().equals(t)) {
              hostList.add(o);
              break;
            }
          }
        }
        
      }
    }

    Iterator<CorusHost> itr = hostList.iterator();
    List<Runnable> invokers = new ArrayList<Runnable>(hostList.size());
    while (itr.hasNext()) {
      final CorusHost addr = itr.next();

      Runnable invoker = new Runnable() {
        Object module;
        Object returnValue;

        @SuppressWarnings("rawtypes")
        @Override
        public void run() {
          Corus corus = (Corus) cachedStubs.get(addr.getEndpoint().getServerAddress());

          if (corus == null) {
            try {
              corus = (Corus) Hub.connect(addr.getEndpoint().getServerAddress());
              cachedStubs.put(addr.getEndpoint().getServerAddress(), corus);
            } catch (java.rmi.RemoteException e) {
              results.decrementInvocationCount();
              return;
            }
          }

          try {
            module = corus.lookup(moduleInterface.getName());
            returnValue = method.invoke(module, params);
            results.addResult(new Result(addr, returnValue, Result.Type.forClass(method.getReturnType())));
          } catch (Exception err) {
            results.decrementInvocationCount();
          }

        }
      };
      invokers.add(invoker);
      results.incrementInvocationCount();
    }

    for (Runnable invoker : invokers) {
      executor.execute(invoker);
    }
  }

  // --------------------------------------------------------------------------
  // Unimplemented
  
  @Override
  public void reconnect() {
  }
  
  @Override
  public void reconnect(String host, int port) {
  }
}
