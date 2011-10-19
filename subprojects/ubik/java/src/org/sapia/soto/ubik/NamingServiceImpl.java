package org.sapia.soto.ubik;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.sapia.soto.Debug;
import org.sapia.soto.Service;
import org.sapia.ubik.mcast.EventChannel;
import org.sapia.ubik.rmi.Consts;
import org.sapia.ubik.rmi.naming.remote.JNDIServerHelper;
import org.sapia.ubik.rmi.naming.remote.RemoteInitialContextFactory;
import org.sapia.ubik.rmi.naming.remote.discovery.DiscoveryHelper;
import org.sapia.ubik.rmi.naming.remote.discovery.JndiDiscoListener;
import org.sapia.ubik.rmi.naming.remote.discovery.ServiceDiscoListener;
import org.sapia.ubik.rmi.naming.remote.proxy.ReliableLocalContext;

/**
 * This service binds objects to a remote JNDI server. It is used by the Ubik
 * layer.
 * 
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2003 <a
 *         href="http://www.sapia-oss.org">Sapia Open Source Software </a>. All
 *         Rights Reserved.</dd>
 *         </dt>
 *         <dt><b>License: </b>
 *         <dd>Read the license.txt file of the jar or visit the <a
 *         href="http://www.sapia-oss.org/license.html">license page </a> at the
 *         Sapia OSS web site</dd>
 *         </dt>
 *         </dl>
 */
public class NamingServiceImpl implements Service, NamingService, JndiDiscoListener, EventChannelProvider {
  private String         _domain;
  private String         _host, _addr;
  private int            _port, _mcastPort;
  private Context        _ctx;
  private Set            _toBind = Collections.synchronizedSet(new HashSet());
  private DiscoveryHelper _helper;

  /**
   * @param host
   *          the host of the remote Ubik JNDI server.
   */
  public void setJndiHost(String host) {
    _host = host;
  }

  /**
   * @param port
   *          the port of the remote Ubik JNDI server.
   */
  public void setJndiPort(int port) {
    _port = port;
  }

  /**
   * @param domain
   *          the domain of the remote Ubik JNDI server.
   */
  public void setDomain(String domain) {
    _domain = domain;
  }
  
  /**
   * @param addr the multicast address to use for discovering remote
   * JNDI servers.
   */
  public void setMulticastAddress(String addr){
    _addr = addr;
  }

  /**
   * @param port the multicast port to use for discovering remote
   * JNDI servers.
   */
  public void setMulticastPort(int port){
    _mcastPort = port;
  }
  
  public EventChannel getEventChannel() {
    return _helper.getChannel();
  }

  public synchronized void bind(String name, Object o) throws NamingException {
    if(_ctx == null){
      _toBind.add(new Binding(name, o));
      return;
    }
    if(Debug.DEBUG){
      Debug.debug(getClass(), "Binding: " + name + ", " + o + " - to context: " + _ctx);
    }
    _ctx.bind(name, o);
  }

  public synchronized Object lookup(String name) throws NamingException,
      NameNotFoundException {
    if(_ctx == null){
      throw new NamingException("No connection to JNDI");
    }
    return _ctx.lookup(name);
  }
  
  public void register(ServiceDiscoListener listener){
    if(_helper != null){
      _helper.addServiceDiscoListener(listener);
    }
  }
  
  public void unregister(ServiceDiscoListener listener){
    if(_helper != null){
      _helper.removeServiceDiscoListener(listener);
    }    
  }        

  /**
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
    try {
      if(_ctx != null)
        _ctx.close();
      if(_helper != null)
        _helper.close();
    } catch(NamingException e) {
      //noop
    }
  }

  /**
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
    if(_port == 0){
      _port = 1099;
    }
    if(_host == null){
      _host = "localhost";
    }
    if(_mcastPort == 0){
      _mcastPort = Consts.DEFAULT_MCAST_PORT;
    }
    if(_addr == null){
      _addr = Consts.DEFAULT_MCAST_ADDR;
    }
    if(_domain == null){
      _domain = JNDIServerHelper.DEFAULT_DOMAIN;
    }
    try{
      _ctx = getInitialContext(_domain, _host, _port);      
      Debug.debug(getClass(), "Got JNDI initial context");
      EventChannel channel = ReliableLocalContext.currentContext().getEventChannel();
      if(channel != null){
        _helper = getDiscoHelper(channel);
        Debug.debug(getClass(), "Got discovery helper");
      }
    }catch(NamingException e){
      Debug.debug(getClass(), "Could not get JNDI initial context for (domain:host:port):" + 
        _domain + ":" + _host + ":" + _port);
      _helper = getDiscoHelper(_domain, _addr, _mcastPort);
      _helper.addJndiDiscoListener(this);
    }
  }

  /**
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {
  }

  public void onJndiDiscovered(Context context) {
    Debug.debug(getClass(), "Discovered JNDI");
    _ctx = context;
    _helper.removeJndiDiscoListener(this);
    synchronized(this){
      Iterator entries = _toBind.iterator();
      while(entries.hasNext()){
        Binding entry = (Binding)entries.next();
        if(entry.getObject() != null){
          try{
            _ctx.bind(entry.getName(), entry.getObject());
            entries.remove();
          }catch(NamingException e){}
        }
      }
    }
  }
  
  protected DiscoveryHelper getDiscoHelper(String domain, 
                                           String mcastHost, 
                                           int mcastPort) throws IOException{
    return new DiscoveryHelper(domain, mcastHost, mcastPort);
  }
  
  protected DiscoveryHelper getDiscoHelper(EventChannel channel) throws IOException{
    return new DiscoveryHelper(channel);
  }  
  
  protected InitialContext getInitialContext(String domain, String host, int port)
    throws NamingException{
    Properties props = new Properties();
    props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
        RemoteInitialContextFactory.class.getName());
    props.setProperty(RemoteInitialContextFactory.UBIK_DOMAIN_NAME, _domain);
    props.setProperty(Context.PROVIDER_URL, "ubik://" + _host + ":" + _port);
    return new InitialContext(props);          
  }
  
  static class Binding{
    SoftReference _object;
    String _name;
    Binding(String name, Object o){
      _name = name;
      _object = new SoftReference(o);
    }
    Object getObject(){
      return _object.get();
    }
    String getName(){
      return _name;
    }
  }
  
}
