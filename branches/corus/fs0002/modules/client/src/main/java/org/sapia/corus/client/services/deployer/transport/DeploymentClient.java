package org.sapia.corus.client.services.deployer.transport;

import java.io.IOException;
import java.io.InputStream;

import org.sapia.corus.client.common.ProgressQueue;
import org.sapia.ubik.net.ServerAddress;

/**
 * This interface specifies the behavior of client-side deployment.
 * 
 * @author Yanick Duchesne
 */
public interface DeploymentClient {

	/**
	 * Connects to the Corus server corresponding to the given address.
	 *  
	 * @param addr the <code>ServerAddress</code> of the server.
	 * @throws IOException if no connection could be made.
	 */
	public void connect(ServerAddress addr) throws IOException;
		
  /**
   * Performs a deployment.
   * 
   * @param meta a <code>DeploymentMetadata</code> holding deployment
   * information used by this instance.
   * @param is the stream of data to deploy.
   * @throws IOException if a problem occurs during deployment.
   */
	public ProgressQueue deploy(DeploymentMetadata meta,
										          InputStream is) throws IOException;
										 
	/**
	 * Releases all system resources that this instance holds.
	 */									 
	public void close();

}
