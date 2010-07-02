package org.sapia.corus.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import org.sapia.corus.client.Corus;
import org.sapia.ubik.net.TCPAddress;

/**
 * Encapsulates the state pertaining to a corus server.
 * 
 * @author yduchesne
 *
 */
public class ServerContextImpl implements ServerContext {

  static final String CORUS_PROCESS_FILE = "corus_process";

  private Corus corus;
  private String serverName = UUID.randomUUID().toString().substring(0, 8);
  private String domain;
  private TCPAddress serverAddress;
  private CorusTransport transport;
  private InternalServiceContext services;
  private String homeDir;
  
  public ServerContextImpl(
      Corus corus,
      CorusTransport transport,
      TCPAddress addr, 
      String domain, 
      String homeDir, 
      InternalServiceContext services){
    this(corus, transport, domain, homeDir, services);
    this.serverAddress = addr;
  }
  
  public ServerContextImpl(
      Corus corus,
      CorusTransport transport,
      String domain, 
      String homeDir, 
      InternalServiceContext services){
    this.corus = corus;
    this.transport = transport;
    this.domain = domain;
    this.homeDir = homeDir;
    this.services = services;
  }
  
  @Override
  public Corus getCorus() {
    return corus;
  }
  
  @Override
  public String getServerName() {
    return serverName;
  }
  
  void setServerName(String serverName) {
    this.serverName = serverName;
  }

  @Override
  public void overrideServerName(String serverName) {
    this.serverName = serverName;
  }
  
  @Override
  public String getHomeDir() {
    return homeDir;
  }
  
  @Override
  public String getDomain() {
    return domain;
  }
  
  @Override  
  public TCPAddress getServerAddress() {
    return serverAddress;
  }
  
  @Override
  public CorusTransport getTransport() {
    return transport;
  }
  
  @Override
  public InternalServiceContext getServices() {
    return services;
  }
  
  @Override
  public <S> S lookup(Class<S> serviceInterface){
    return services.lookup(serviceInterface);
  }
  
  /**
   * This method returns properties that can be defined for all processes managed
   * by all Corus servers on this host, or for processes that are part of a 
   * given domain on this host.
   * <p>
   * Properties must be specified in Java property files under the Corus home
   * directory. For multi-domain properties, a file named 
   * <code>corus_process.properties</code> is searched. For domain-specific
   * properties, a file named <code>corus_process_someDomain.properties</code>
   * is searched.
   * <p>
   * Domain properties override global (multi-domain) properties.
   * <p>
   * The properties are passed to the processes upon their startup.
   */
  @Override
  public Properties getProcessProperties() throws IOException{
    File home = new File(getHomeDir() + File.separator + "config");
    File globalProps = new File(home, CORUS_PROCESS_FILE + ".properties");
    Properties globals = new Properties();
    if(globalProps.exists()){
      FileInputStream stream = new FileInputStream(globalProps);
      try{
        globals.load(stream);
      }finally{
        stream.close();
      }
    }
    File domainProps = new File(home, CORUS_PROCESS_FILE + "_" + getDomain() + ".properties");
    if(domainProps.exists()){
      FileInputStream stream = new FileInputStream(domainProps);
      try{
        globals.load(stream);
      }finally{
        stream.close();
      }
    }    
    return globals;
  }
  
  void setServerAddress(TCPAddress addr){
    this.serverAddress = addr;
  }

}
