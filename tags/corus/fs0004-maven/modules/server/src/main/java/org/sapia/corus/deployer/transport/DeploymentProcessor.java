package org.sapia.corus.deployer.transport;

import org.apache.log.Logger;
import org.sapia.corus.core.CorusRuntime;
import org.sapia.corus.deployer.transport.http.HttpDeploymentAcceptor;
import org.sapia.corus.deployer.transport.mplex.MplexDeploymentAcceptor;
import org.sapia.ubik.rmi.server.transport.http.HttpTransportProvider;
import org.sapia.ubik.rmi.server.transport.socket.MultiplexSocketTransportProvider;

/**
 * @author Yanick Duchesne
 */
public class DeploymentProcessor {
	
	private DeploymentAcceptor _acceptor;
	private DeploymentConnector _connector;	
	private Logger _logger;
	
	public DeploymentProcessor(DeploymentConnector connector, Logger logger){
		_logger    = logger;
		_connector = connector;
	}
	
  public void init() throws Exception{
    if(CorusRuntime.getTransport().getTransportProvider() instanceof MultiplexSocketTransportProvider){
    	MultiplexSocketTransportProvider provider = (MultiplexSocketTransportProvider)CorusRuntime.getTransport().getTransportProvider();
    	_acceptor = new MplexDeploymentAcceptor(provider, _logger);
    	_acceptor.registerConnector(_connector);
    }
    else if(CorusRuntime.getTransport().getTransportProvider() instanceof HttpTransportProvider){
    	HttpTransportProvider provider = (HttpTransportProvider)CorusRuntime.getTransport().getTransportProvider();
    	_acceptor = new HttpDeploymentAcceptor(provider);
			_acceptor.registerConnector(_connector);    	
    }
    else{
    	throw new IllegalStateException("Transport provider not recognized; expecting mplex or http");
    }
    _acceptor.init();
  }
  
  public void start() throws Exception{
  	_acceptor.start();
  }
  
  public void dispose(){
  	try{
			_acceptor.stop();  		
  	}catch(Exception e){
  		// noop
  	}

  }
}
