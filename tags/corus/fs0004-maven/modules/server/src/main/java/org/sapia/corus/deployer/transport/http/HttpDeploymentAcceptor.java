package org.sapia.corus.deployer.transport.http;

import org.sapia.corus.client.services.deployer.transport.http.HttpDeploymentClient;
import org.sapia.corus.deployer.transport.Deployment;
import org.sapia.corus.deployer.transport.DeploymentAcceptor;
import org.sapia.corus.deployer.transport.DeploymentConnector;
import org.sapia.ubik.rmi.server.transport.http.HttpTransportProvider;
import org.simpleframework.http.core.Container;

/**
 * Implements the <code>DeploymentAcceptor</code> interface over a <code>HttpTransportProvider</code>.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class HttpDeploymentAcceptor implements Container, DeploymentAcceptor{
	
	private HttpTransportProvider _provider;
	private DeploymentConnector   _connector;
	
	public HttpDeploymentAcceptor(HttpTransportProvider provider){
		_provider  = provider;
	}
	
	/**
   * @see org.sapia.corus.deployer.transport.DeploymentAcceptor#init()
   */
  public void init() throws Exception {
  }
  
  /**
   * @see org.sapia.corus.deployer.transport.DeploymentAcceptor#start()
   */
  public void start() throws Exception {
		_provider.getContainerMap().addService(HttpDeploymentClient.DEPLOYER_CONTEXT, this);  	
  }
  
  /**
   * @see org.sapia.corus.deployer.transport.DeploymentAcceptor#stop()
   */
  public void stop() throws Exception {
  }
  
  /**
   * @see org.sapia.corus.deployer.transport.DeploymentAcceptor#registerConnector(DeploymentConnector)
   */
  public void registerConnector(DeploymentConnector connector) {
    _connector = connector;	
  }
  
  @Override
  public void handle(org.simpleframework.http.Request req,
      org.simpleframework.http.Response res) {
  	_connector.connect(new Deployment(new HttpConnection(req, res)));
  }

}
