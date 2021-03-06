package org.sapia.corus.client.services.cluster;

import java.io.IOException;
import java.util.Set;

import org.sapia.corus.client.Module;
import org.sapia.ubik.mcast.EventChannel;
import org.sapia.ubik.mcast.Response;
import org.sapia.ubik.mcast.TimeoutException;
import org.sapia.ubik.net.ServerAddress;

/**
 * This module provides clustering services (it is in charge of discovering
 * other Corus instances in the cluster, and reporting its own Corus instance to
 * the cluster).
 * 
 * @author Yanick Duchesne
 */
public interface ClusterManager extends java.rmi.Remote, Module {

  public static String ROLE = ClusterManager.class.getName();

  /**
   * Returns addresses of the other Corus servers in the cluster/domain.
   * 
   * @return a {@link Set} of {@link ServerAddress} instances.
   */
  public Set<ServerAddress> getHostAddresses();

  /**
   * Returns information of the other Corus servers in the cluster/domain.
   * 
   * @return a {@link Set} of {@link CorusHost} instances.
   */
  public Set<CorusHost> getHosts();

  /**
   * Returns the event channel used to dispatch events to the other Corus nodes
   * in the cluster.
   * <p>
   * IMPORTANT: this method cannot be called remotely.
   * 
   * @return an {@link EventChannel} instance.
   */
  public EventChannel getEventChannel();

  /**
   * @return the {@link ClusterStatus} for the Corus instance.
   */
  public ClusterStatus getClusterStatus();

  /**
   * Forces a resync of this node with the other nodes in the cluster.
   */
  public void resync();

  /**
   * @param notif
   *          a {@link ClusterNotification} to send.
   * @return the {@link Response} returned by the targeted node.
   * @throws IOException
   *           if an IO problem occurs.
   * @throws TimeoutException
   *           if no response could be obtained from the next target node in a
   *           timely fashion.
   */
  public Response send(ClusterNotification notif) throws IOException, TimeoutException;

}
