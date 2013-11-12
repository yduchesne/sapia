package org.sapia.ubik.mcast.control;

import java.io.IOException;
import java.util.Set;

import org.sapia.ubik.mcast.EventChannel;
import org.sapia.ubik.net.ServerAddress;

/**
 * This interface is meant to abstract the {@link EventChannel} class from the
 * {@link EventChannelController}. To the controller, an instance of this class
 * represents its event channel.
 * 
 * @author yduchesne
 *
 */
public interface ChannelCallback {
	
	/**
	 * @return the {@link String} identifier of the node corresponding to the underlying event channel.
	 */
	public String getNode();
	
	/**
	 * @return the unicast {@link ServerAddress} of the underlying event channel.
	 */
	public ServerAddress getAddress();
	
	/**
	 * @return the {@link Set} of node identifiers corresponding to the nodes
	 *  in the domain/cluster.
	 */
	public Set<String> getNodes();
	
	/**
	 * Triggers a resync with the cluster.
	 */
	public void resync();
	
	/**
	 * Sends the given {@link ControlRequest}.
	 * 
	 * @param req a {@link ControlRequest} to send.
	 */
	public void sendRequest(ControlRequest req);
	
	/**
	 * @param targetedNodes the set of node identifiers corresponding to the targeted nodes.
	 * @param request the {@link SynchronousControlRequest} to send.
	 * @return the {@link Set} of {@link SynchronousControlResponse} that are returned in response to the request.
	 * @throws InterruptedException if the calling thread is interrupted while sending the request.
	 * @throws IOException if an IO problem occurred while sending the request.
	 */
	public Set<SynchronousControlResponse> sendSynchronousRequest(Set<String> targetedNodes, SynchronousControlRequest request)
		throws InterruptedException, IOException;
	
	/**
	 * Sends a {@link ControlResponse} back to the master node - following a corresponding
	 * request.
	 * @param masterNode the {@link String} corresponding to the identifier of the master node.
	 * @param res the {@link ControlResponse} to send.
	 */
	public void sendResponse(String masterNode, ControlResponse res);
	
	/**
	 * Sends the given {@link ControlNotification}.
	 * 
	 * @param notif a {@link ControlNotification}.
	 */
	public void sendNotification(ControlNotification notif);
	
	/**
	 * This method is meant to notify the underlying event channel that a given node has provided
	 * its heartbeat.
	 * 
	 * @param a {@link String} corresponding to the identifier of the node that provided
	 * its heartbeat.
	 * @param unicastAddress the unicast {@link ServerAddress} of the given node.
	 */
	public void heartbeat(String node, ServerAddress unicastAddress);

	/**
	 * This method is meant to notify the underlying event channel that a given node has been
	 * detected as being down.
	 * 
	 * @param node a {@link String} corresponding to the identifier of the node that was detected
	 * as being down.
	 */
	public void down(String node);
		
}