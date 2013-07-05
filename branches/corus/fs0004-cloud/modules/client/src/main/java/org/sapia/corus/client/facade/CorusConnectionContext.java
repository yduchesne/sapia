package org.sapia.corus.client.facade;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.sapia.corus.client.ClusterInfo;
import org.sapia.corus.client.Corus;
import org.sapia.corus.client.Result;
import org.sapia.corus.client.Results;
import org.sapia.corus.client.Results.ResultListener;
import org.sapia.corus.client.cli.ClientFileSystem;
import org.sapia.corus.client.exceptions.CorusException;
import org.sapia.corus.client.exceptions.cli.ConnectionException;
import org.sapia.corus.client.facade.impl.ClientSideClusterInterceptor;
import org.sapia.corus.client.services.cluster.ClusterManager;
import org.sapia.corus.client.services.cluster.CorusHost;
import org.sapia.ubik.net.ServerAddress;
import org.sapia.ubik.rmi.server.Hub;
import org.sapia.ubik.rmi.server.invocation.ClientPreInvokeEvent;
import org.sapia.ubik.rmi.server.transport.http.HttpAddress;

/**
 * An instance of this class encapsulates objects pertaining to the connection to a
 * Corus server.
 * 
 * @author yduchesne
 *
 */
public class CorusConnectionContext {

  static final int INVOKER_THREADS 		 = 10;
  static final long RECONNECT_INTERVAL = 15000;
  
  private long 												 lastReconnect = System.currentTimeMillis();
  private Corus 											 corus;
  private ServerAddress 					     connectAddress;
  private CorusHost 									 serverHost;
  private String 										   domain;
  private Map<Class<?>, Object> 		   modules 		 = Collections.synchronizedMap(new HashMap<Class<?>, Object>());
  private Set<CorusHost> 						   otherHosts  = Collections.synchronizedSet(new HashSet<CorusHost>());
  private Map<ServerAddress, Corus> 	 cachedStubs = Collections.synchronizedMap(new HashMap<ServerAddress, Corus>());
  private Stack<ServerAddress>         connectionHistory = new Stack<ServerAddress>();
  private ExecutorService 						 executor;
  private ClientSideClusterInterceptor interceptor;
  private ClientFileSystem             fileSys;

  /**
   * @param host the host of the Corus server to connect to.
   * @param port the port of the Corus server to connect to.
   * @param fileSys the {@link ClientFileSystem}.
   * @param invokerThreads the number of threads to use when dispatching clustered method calls
   * to targeted Corus instances.
   * @throws Exception if a problem occurs when attempting to connect to the Corus server.
   * @throws ConnectionException if not Corus server exists at the given host/port, or if a network-related
   * problem occurs while attempting to connect to the given host/port.
   */
  public CorusConnectionContext(String host, int port, ClientFileSystem fileSys, int invokerThreads) throws ConnectionException, Exception {
    reconnect(host, port);
    interceptor = new ClientSideClusterInterceptor();
    this.fileSys = fileSys;
    Hub.getModules().getClientRuntime().addInterceptor(ClientPreInvokeEvent.class, interceptor);
    executor = Executors.newFixedThreadPool(invokerThreads);
  }

  public CorusConnectionContext(String host, int port, ClientFileSystem fileSys) throws Exception {
    this(host, port, fileSys, INVOKER_THREADS);
  }

  /**
   * @return the Corus server's version.
   */
  public String getVersion(){
    refresh();
    return corus.getVersion();
  }
  
  /**
   * @return the {@link ClientFileSystem}.
   */
  public ClientFileSystem getFileSystem() {
    return fileSys;
  }

  /**
   * @return the domain/cluster of the Corus server to which this instance is
   *         connected.
   */
  public String getDomain() {
    return domain;
  }

  /**
   * @return the {@link ServerAddress} of the other Corus instances in the
   *         cluster.
   */
  public Collection<CorusHost> getOtherHosts() {
    refresh();
    return Collections.unmodifiableCollection(otherHosts);
  }
  
  /**
   * @return the remote {@link Corus} instance, corresponding to the server's
   *         interface.
   */
  public Corus getCorus() {
    refresh();
    return corus;
  }

  public CorusHost getServerHost() {
    return serverHost;
  }
  
  /**
   * @return the {@link ServerAddress} of the Corus server to which this
   *         instance is connected.
   */
  public ServerAddress getAddress() {
    if (serverHost != null) {
      return serverHost.getEndpoint().getServerAddress();
    } else {
      return connectAddress;
    }
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
    connectAddress = HttpAddress.newDefaultInstance(host, port);
    reconnect();
  }

  /**
   * Reconnects to the corus server that this instance corresponds to.
   * 
   * @throws CorusException
   */
  public synchronized void reconnect() {
    try {
      corus = (Corus) Hub.connect(connectAddress);
      domain = corus.getDomain();
      serverHost = corus.getHostInfo();
      otherHosts.clear();
      cachedStubs.clear();
      modules.clear();

      ClusterManager mgr = (ClusterManager) corus.lookup(ClusterManager.ROLE);
      otherHosts.addAll(mgr.getHosts());
    } catch (RemoteException e) {
      throw new ConnectionException("Could not reconnect to Corus server", e);
    }
  }
  
  /**
   * Disconnects this instance, releases its resources. This instance should not be used thereafter.
   */
  public void disconnect() {
    this.executor.shutdownNow();
  }

  public void clusterCurrentThread(ClusterInfo info) {
    refresh();
    ClientSideClusterInterceptor.clusterCurrentThread(info);
  }

  @SuppressWarnings(value = "unchecked")
  public <T,M> void invoke(Results<T> results, Class<M> moduleInterface, Method method,
      Object[] params, ClusterInfo cluster) throws Throwable {
    refresh();
    
    final List<Result<T>> resultList = new ArrayList<Result<T>>();
    results.addListener(new ResultListener<T>() {
       @Override
      public void onResult(Result<T> result) {
         resultList.add(result);
      }
    });
    FacadeInvocationContext.set(resultList);
    
    try {
      if (cluster.isClustered()) {
        applyToCluster(results, moduleInterface, method, params, cluster);
      }
      else{
        T returnValue = (T) method.invoke(lookup(moduleInterface), params);
        results.incrementInvocationCount();
        results.addResult(new Result<T>(connectAddress, returnValue));
      }
    } catch (InvocationTargetException e) {
      throw e.getTargetException();
    } 
  }
  
  public <T,M> T invoke(Class<T> returnType, Class<M> moduleInterface, Method method, Object[] params, ClusterInfo info) throws Throwable{
    try {
      
      ClientSideClusterInterceptor.clusterCurrentThread(info);
      
      Object toReturn = method.invoke(lookup(moduleInterface), params);
      if(toReturn != null){
        FacadeInvocationContext.set(toReturn);
        return returnType.cast(toReturn);
      } else {
        return null;
      }
    } catch(InvocationTargetException e) {
      throw e.getTargetException();
    }
  }

  @SuppressWarnings(value = "unchecked")
  void applyToCluster(
      final Results<?> results, 
      final Class<?> moduleInterface,
      final Method method, 
      final Object[] params,
      final ClusterInfo cluster) {
    
    List<ServerAddress> hostList = new ArrayList<ServerAddress>();
    if (cluster.isTargetingAllHosts()) {
      hostList.add(connectAddress);
      for (CorusHost otherHost: otherHosts) {
        hostList.add(otherHost.getEndpoint().getServerAddress());
      }
    } else {
      hostList.addAll(cluster.getTargets());
    }
    
    Iterator<ServerAddress> itr = hostList.iterator();
    List<Runnable> invokers = new ArrayList<Runnable>(hostList.size());
    while (itr.hasNext()) {
      final ServerAddress addr = itr.next();
      
      Runnable invoker = new Runnable(){
        Object module;
        Object returnValue;

        @SuppressWarnings("rawtypes")
        @Override
        public void run() {
          Corus corus = (Corus) cachedStubs.get(addr);

          if (corus == null) {
            try {
              corus = (Corus) Hub.connect(addr);
              cachedStubs.put(addr, corus);
            } catch (java.rmi.RemoteException e) {
              results.decrementInvocationCount();
              return;
            }
          }

          try {
            module = corus.lookup(moduleInterface.getName());
            returnValue = method.invoke(module, params);
            results.addResult(new Result(addr, returnValue));
          } catch (Exception err) {
            results.decrementInvocationCount();
          } 

        }
      };
      invokers.add(invoker);
      results.incrementInvocationCount();
    }
    
    for(Runnable invoker:invokers){
      executor.execute(invoker);
    }
  }
  
  /**
   * @return the {@link Stack} of addresses corresponding to the CLI's connection history.
   */
  public Stack<ServerAddress> getConnectionHistory() {
    return connectionHistory;
  }
  
  public synchronized <T> T lookup(Class<T> moduleInterface){
    Object module  = modules.get(moduleInterface);
    if(module == null){
      module = corus.lookup(moduleInterface.getName());
      modules.put(moduleInterface, module);
    }
    return moduleInterface.cast(module);
  }

  protected void refresh() throws ConnectionException {
    if ((System.currentTimeMillis() - lastReconnect) > RECONNECT_INTERVAL) {
      reconnect();
      lastReconnect = System.currentTimeMillis();
    }
  }
}
