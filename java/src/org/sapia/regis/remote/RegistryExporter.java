package org.sapia.regis.remote;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;

import org.sapia.regis.RegisDebug;
import org.sapia.regis.Registry;
import org.sapia.ubik.mcast.AsyncEventListener;
import org.sapia.ubik.mcast.EventChannel;
import org.sapia.ubik.mcast.RemoteEvent;
import org.sapia.ubik.rmi.Consts;
import org.sapia.ubik.rmi.naming.remote.RemoteInitialContextFactory;
import org.sapia.ubik.rmi.server.Hub;
import org.sapia.ubik.rmi.server.invocation.ServerPostInvokeEvent;
import org.sapia.ubik.rmi.server.invocation.ServerPreInvokeEvent;

/**
 * An instance of this class exports a <code>Registry</code> as 
 * a remote object.
 * <p>
 * The <code>Registry</code> that his held by an instance of this
 * class will internally be made available through the network upon
 * one of the <code>bind()</code> methods being called.
 * <p>
 * To lookup a <code>Registry</code> that has been bound by an 
 * instance of this class, applications can use a <code>RegistryImporter</code>,
 * or a <code>RemoteRegistryFactory</code>.
 * 
 * @see #bind(int)
 * @see #bind(String, Properties)
 * @see org.sapia.regis.remote.client.RegistryImporter
 * @see org.sapia.regis.remote.client.RemoteRegistryFactory
 */
public class RegistryExporter implements RemoteConsts, AsyncEventListener{

  public static final String PUBLISH = "org.sapia.regis.remote.pub";
  public static final String DISCO   = "org.sapia.regis.remote.disco";
  
  private RemoteRegistry _reg;
  private boolean _peerToPeer = false;
  private EventChannel _channel;
  private Object _stub;
  private Map _peers = Collections.synchronizedMap(new HashMap());
  private String _domain = Consts.DEFAULT_DOMAIN;
  
  public RegistryExporter(Registry reg, Properties bootstrapProps){
    _reg = new RemoteRegistry(new Authenticator(USERNAME, PASSWORD), reg, bootstrapProps);
  }
  
  public RegistryExporter(String username, String password, Registry reg, Properties bootstrapProps){
    _reg = new RemoteRegistry(new Authenticator(username, password), reg, bootstrapProps);
  }
  
  /**
   * @param peerToPeer indicates if this instance should behave as peer in a network of registries 
   * - by default, this instance is not in peer-to-peer mode.
   */
  public void setPeerToPeer(boolean peerToPeer){
    _peerToPeer = peerToPeer;
  }
  
  /**
   * @param domain the name of the domain to which this instance belongs.
   */
  public void setDomain(String domain){
    _domain = domain;
  }
  
  /**
   * Closes this instance (which releases all system resources it holds).
   */
  public void close(){
    if(_channel != null){
      _channel.close();
    }
    _reg.close();
  }
  
  /**
   * This method bind the <code>Registry</code> held by this
   * instance on the network, on the given port. The registry thus
   * becomes available as a remote object.
   * <p>
   * Clients can connect to the registry through the Ubik <code>Hub</code>:
   * <pre>
   *   // connects on the given host, port
   *   Registry reg = (Registry)Hub.connect("192.168.0.101", 40000);
   * </pre>
   * <p>
   * Note that Ubik runtime attempts to bind servers on addresses that match the
   * value of the <code>ubik.rmi.address-pattern</code> system property. If that
   * property is not defined, then the address used will be the first that DOES NOT
   * correspond to "localhost" or 127.0.0.1. If no such address exists, then "localhost"
   * is internally used.
   * <p>
   * If "localhost" must be used (for testing purposes for example), then the 
   * <code>ubik.rmi.address-pattern</code> system property should be set, with 
   * <code>localhost</code> as a value. 
   * 
   * @param port a port
   * @throws Exception if the registry could not be exported
   */
  public void bind(int port) throws Exception{
    SessionInterceptor interceptor = new SessionInterceptor(_reg);
    System.out.println("Binding server to port: " + port);
    Hub.serverRuntime.addInterceptor(ServerPreInvokeEvent.class, interceptor);
    Hub.serverRuntime.addInterceptor(ServerPostInvokeEvent.class, interceptor);
    _stub = Hub.exportObject(_reg, port);
    publish(System.getProperties());
  }
  
  /**
   * This method binds the <code>Registry</code> held by this
   * instance to the JNDI service that corresponds to the given properties, under
   * the given JNDI name.
   * <p>
   * The properties passed in correspond to the ones expected by Ubik's JNDI provider
   * (see the <a href="http://www.sapia-oss.org/projects/ubik/naming.html#robust">tutorial</a>)
   * <ul>
   *   <li><b>java.naming.provider.url</b>: this is a property specified by the JNDI spec. It is interpreted
   *   by Ubik as corresdoning to the remote JNDI server to connect to (example 
   *   <code>ubik://localhost:1099</code>). 
   *   <li><b>ubik.jndi.domain</b>: the logical domain name of the a remote JNDI server to 
   *   connect to, if one could not be found on the specified URL. 
   *   <li><b>ubik.rmi.naming.mcast.address</b>: the multicast address to use when trying to perform
   *   client-side discovery of remote a JNDI server corresponding to the specified domain - see
   *    previous property (the address defaults to 224.0.0.1). 
   *   <li><b>ubik.rmi.naming.mcast.port</b>: the multicast port to use when trying to perform
   *   client-side discovery of remote a JNDI server corresponding to the specified domain - see
   *   previous property (the port defaults to 5454).   
   * </ul>
   * <p>
   * Client applications can lookup a registry exported by this instance using a <code>RegistryImporter</code>,
   * or a <code>RemoteRegistryFactory</code>.
   * <p>
   * For more info concerning the properties defined above, see the javadoc for the  
   * <a href="http://www.sapia-oss.org/projects/ubik/api/org/sapia/ubik/rmi/Consts.html">
   * Const</a> class.
   * 
   * @param jndiName the JNDI name under which the registry should be
   * bound in the Ubik JNDI service.
   * @param props must hold Ubik JNDI properties
   * @throws Exception if the registry could not be bound to the JNDI service.
   * 
   * @see <code>RegistryImporter</code>
   */
  public void bind(String jndiName, Properties props) throws Exception{
    SessionInterceptor interceptor = new SessionInterceptor(_reg); 
    Hub.serverRuntime.addInterceptor(ServerPreInvokeEvent.class, interceptor);
    Hub.serverRuntime.addInterceptor(ServerPostInvokeEvent.class, interceptor);      
    props.setProperty(InitialContext.INITIAL_CONTEXT_FACTORY, RemoteInitialContextFactory.class.getName());
    InitialContext ctx = new InitialContext(props);
    _stub = Hub.exportObject(_reg);
    ctx.bind(jndiName, _reg);
    ctx.close();
    publish(props);
  }
  
  private void publish(Properties props) throws IOException{
    if(_peerToPeer){
      String mcastHost = props.getProperty(Consts.MCAST_ADDR_KEY, Consts.DEFAULT_MCAST_ADDR);
      int    mcastPort = Integer.parseInt(props.getProperty(Consts.MCAST_PORT_KEY, ""+Consts.DEFAULT_MCAST_PORT));
      _channel = new EventChannel(_domain, mcastHost, mcastPort);
      _channel.registerAsyncListener(PUBLISH, this);
      _channel.registerAsyncListener(DISCO, this);
      _channel.start();
      _channel.dispatch(PUBLISH, _stub);
      _reg.setPeers(_peers);
    }
  }
  
  public void onAsyncEvent(RemoteEvent evt) {
    if(evt.getType().equals(PUBLISH)){
      try{
        RegisDebug.debug(this, "New peer was published");        
        synchronized(_peers){
          if(!_peers.containsKey(evt.getNode())){
            _peers.put(evt.getNode(), evt.getData());
          }
          _channel.dispatch(DISCO, _stub);
        }
      }catch(IOException e){
        e.printStackTrace();
      }
    }
    else if(evt.getType().equals(DISCO)){
      try{
        RegisDebug.debug(this, "Discovered peer");
        synchronized(_peers){
          if(!_peers.containsKey(evt.getNode())){
            _peers.put(evt.getNode(), evt.getData());
          }
        }
      }catch(IOException e){
        e.printStackTrace();
      }
    }
  }
}
