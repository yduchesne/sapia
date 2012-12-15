package org.sapia.corus.client.services.deployer.transport;

import java.util.HashSet;
import java.util.Set;

import org.sapia.ubik.net.ServerAddress;
import org.sapia.ubik.rmi.server.VmId;

/**
 * Models meta-information about a given deployment.
 * 
 * @author Yanick Duchesne
 */
public class DeploymentMetadata implements java.io.Serializable{
	
  static final long serialVersionUID = 1L;
  
	private VmId origin = VmId.getInstance();
	
	private Set<ServerAddress> visited 		= new HashSet<ServerAddress>();
	private Set<ServerAddress> targeted   = new HashSet<ServerAddress>();
	private String 						 fileName;
	private long 							 contentLen;
	private boolean 					 clustered;
	
	public DeploymentMetadata(String fileName, long contentLen, boolean clustered){
		this.fileName = fileName;
		this.contentLen = contentLen;
		this.clustered = clustered;
	}
	
	/**
	 * @return the file name of the deployed archive.
	 */
	public String getFileName(){
		return fileName;
	}
	
	/**
	 * @return the length (in bytes) of the deployed archive.
	 */
	public long getContentLength(){
		return contentLen;
	}
	
	/**
	 * @return <code>true</code> if deployment is clustered.
	 */
	public boolean isClustered(){
		return clustered;
	}
	
	/**
	 * Returns the addresses of the servers to which the deployment has been successfully uploaded.
	 *  
	 * @return a {@link Set} of {@link ServerAddress} instances.
	 */
	public Set<ServerAddress> getVisited(){
		return visited;
	}
	
	/**
	 * @return the {@link Set} of {@link ServerAddress} instances that are targeted.
	 */
	public Set<ServerAddress> getTargeted() {
    return targeted;
  }
	
	/**
	 * @param addr a {@link ServerAddress}.
	 * @return <code>true</code> if the given address corresponds to a node that is
	 * target by this deployment.
	 */
	public boolean isTargeted(ServerAddress addr) {
	  return !targeted.isEmpty() && targeted.contains(addr);
	}
	
	/**
	 * @return the {@link VmId} of the server from which this instance originates. 
	 */
	public VmId getOrigin(){
	  return origin;	
	}
}
