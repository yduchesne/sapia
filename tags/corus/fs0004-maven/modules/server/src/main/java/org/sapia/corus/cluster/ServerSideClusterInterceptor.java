package org.sapia.corus.cluster;

import org.apache.log.Logger;
import org.sapia.corus.client.ClusterInfo;
import org.sapia.corus.client.Module;
import org.sapia.corus.client.services.cluster.ClusterManager;
import org.sapia.corus.client.services.cluster.ClusteredCommand;
import org.sapia.corus.client.services.cluster.ClusteredInvoker;
import org.sapia.corus.core.CorusRuntime;
import org.sapia.ubik.rmi.interceptor.Interceptor;
import org.sapia.ubik.rmi.replication.ReplicationEvent;
import org.sapia.ubik.rmi.server.invocation.ServerPreInvokeEvent;

/**
 * @author Yanick Duchesne
 */
public class ServerSideClusterInterceptor implements Interceptor {
  private static ThreadLocal<ClusterInfo> _registration = new ThreadLocal<ClusterInfo>();
  private Logger             _log;
  private ClusterManager     _cluster;

  public ServerSideClusterInterceptor(ClusterManager cluster) {
    _cluster = cluster;
  }

  ServerSideClusterInterceptor(Logger log) {
    _log = log;
  }

  public static void clusterCurrentThread(ClusterInfo cluster) {
    _registration.set(cluster);
  }
  
  public void onServerPreInvokeEvent(ServerPreInvokeEvent evt){
		if (evt.getInvokeCommand() instanceof ClusteredCommand) {
			ClusteredCommand cmd = (ClusteredCommand) evt.getInvokeCommand();

			if (evt.getTarget() instanceof Module) {
				try {
					ClusteredInvoker invoker = (ClusteredInvoker)cmd.getReplicatedInvoker();
					invoker.setUp(CorusRuntime.getCorus(), _cluster);
					invoker.setModuleName(((Module)evt.getTarget()).getRoleName());
					  																									
				} catch (Throwable t) {
					_log.error("Could not cluster command", t);
				}
			}
			else{
				cmd.disable();
			}
		}
  }

  public void onReplicationEvent(ReplicationEvent evt) {
    if (evt.getReplicatedCommand() instanceof ClusteredCommand) {
    	ClusteredInvoker invoker = (ClusteredInvoker)((ClusteredCommand)evt.getReplicatedCommand()).getReplicatedInvoker();
     	invoker.setUp(CorusRuntime.getCorus(), _cluster);
    }
  }

  /*
  private static boolean isCurrentThreadClustered() {
		ClusterInfo clustered = (ClusterInfo) _registration.get();

    if (clustered == null) {
      return false;
    } else {
      return clustered.isClustered();
    }
  }*/
}
