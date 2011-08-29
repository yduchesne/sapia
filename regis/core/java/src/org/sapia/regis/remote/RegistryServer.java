package org.sapia.regis.remote;

import java.util.Properties;

import org.sapia.regis.Configurable;
import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.RWNode;
import org.sapia.regis.RWSession;
import org.sapia.regis.RegisDebug;
import org.sapia.regis.RegisLog;
import org.sapia.regis.RegisSession;
import org.sapia.regis.Registry;
import org.sapia.regis.RegistryContext;
import org.sapia.regis.loader.RegistryConfigLoader;
import org.sapia.regis.util.Utils;
import org.sapia.ubik.rmi.Consts;
import org.sapia.ubik.rmi.server.Hub;

/**
 * <h2>Basics</h2>
 * An instance of this class connects to a registry and exports it as
 * a remote registry to a Ubik JNDI server, or on an explicit port.
 * <p>
 * This class is configured through Java properties. The path to a Java properties
 * file can be specified at the command-line. This class attempts to load the file from:
 * <ul>
 *   <li>the file system.
 *   <li>the network (interpreting the given path as a URL).
 *   <li>the classpath.
 * </ul>
 * <p>
 * If no such path is given, this class defaults to the system properties - in fact
 * this class also defaults to the system properties if a given property could not 
 * be found at the level of the loaded properties.
 * <p>
 * 
 * This class expects one of the following properties to be specified:
 * <ul>
 *   <li>org.sapia.regis.jndi.name: corresponds to the JNDI name under which to bind
 *   the registry, in the Ubik JNDI server.
 *   <li>org.sapia.regis.server.port: corresponds to the explicit port to which to bind the 
 *   registry (in this case the Ubik JNDI server is not used).
 * </ul>
 * <p>
 * If the host on which the server is started has multiple network interfaces, then the one
 * to specifically use can be set through the following system property 
 * (see the Ubik <a target="ubikNaming" href="http://www.sapia-oss.org/projects/ubik/tutorial.html#bindaddr">documentation</a>):
 * <code>ubik.rmi.address-pattern</code>. The value of the property must correspond to a valid regular
 * expression (for example: <code>192\.\d+\.\d+\.\d+</code>).
 * <p>
 * The following provides a sample registry server configuration:
 * <pre>
 * org.sapia.regis.factory=org.sapia.regis.local.LocalRegistryFactory
 * org.sapia.regis.remote.jndi.name=services/regis
 * java.naming.provider.url=ubik://192.168.0.103:1099/
 * ubik.jndi.domain=default
 * org.sapia.regis.remote.p2p=true
 * ubik.rmi.address-pattern=192\.\d+\.\d+\.\d+
 * </pre>
 * The properties are further explained below.
 * 
 * <p>
 * Note that if the Ubik JNDI server is used in order to publish the server on the network, 
 * the required Ubik JNDI properties must be given as part of properties loaded by this 
 * server (again, see the Ubik <a target="ubikNaming" href="http://www.sapia-oss.org/projects/ubik/naming.html#robust">documentation</a>).
 * 
 * <h2>Remote Configuration Upload</h2>
 * 
 * A registry server supports remotely loading configuration files (all remote
 * registry nodes implement the {@link org.sapia.regis.Configurable} interface).
 * However, since this can pause a security or access control issue, a registry server supports
 * username/password authentication. That is: as part of the server's configuration properties, a username
 * and password can be specified, which are used to perform authentication of remote users that
 * attempt loading a configuration into the registry.
 * <p>
 * The username and password must be given as values of the following properties, respectively:
 * <ul>
 *   <li>org.sapia.regis.remote.username
 *   <li>org.sapia.regis.remote.password
 * </ul>
 * If no username and/or password are given, the following default ones are respectively used:
 * <ul>
 *   <li>regis
 *   <li>secret
 * </ul>
 * 
 * <h2>Bootstrap Configuration</h2>
 * 
 * A registry server can be provided with a boostrap XML configuration, that it will load at startup to initialize
 * itself. This can be useful between shutdowns, if the configuration that a registry contains is not persisted. 
 * For bootstrapping to work, the following property must be specified as part of the registry server's properties: 
 * <code>org.sapia.regis.remote.bootstrap</code>. The value of that property consists of a comma-delimited list
 * of resources that are to be loaded. Each resource is interpreted either as (whichever works first):
 *
 * <ul>
 *   <li>a file.
 *   <li>a URL.
 *   <li>a resource on the classpath.
 * </ul>
 * 
 * The following is an example of a bootstrap property:
 * <pre>
 * org.sapia.regis.remote.bootstrap=/home/conf/host.xml, http://mercury.acmeco.net/global.xml
 * </pre>
 * Attempt is made to load all resources (loading does not stop at the first resource that is found).
 * 
 * <h2>Peer-to-Peer</h2>
 * 
 * A registry server can act in peer-to-peer mode, meaning that in this case all registry
 * servers belonging to the same given domain will upload their configuration to their peers.
 * For example, given registry A and B belonging to the same domain, if registry A is
 * updated with a configuration by an end-user, it will in turn upload that configuration
 * to registry B.
 * <p>
 * <b>Replication is not performed in the case of boostrap configuration.</b>
 * 
 * <p>
 * The domain of a registry server is passed to it through the properties that it loads. It
 * is identified by the following property: <code>ubik.jndi.domain</code> (the server uses
 * Ubik's multicast to discover other registry servers in the domain, and publish itself to these
 * peers). If the property is not specified, <code>default</code> is used as domain name.
 * <p>
 * In addition, for the replicated mechanism to work, the following property also has to be 
 * specified: <code>org.sapia.regis.remote.p2p</code>, with a value of <code>true</code>.
 * <p>
 * The {@link org.sapia.regis.ant.RegistryTask} Ant task has been implemented in order to  
 * upload registry configuration remotely, from an Ant script.
 * 
 * @author yduchesne
 *
 */
public class RegistryServer implements RemoteConsts{
  
  private static RegistryExporter _exporter;
  private static Properties _bootstrapProps = new Properties();
  public static boolean startThread = true;
  
  
  public static void main(String[] args) {
    if(args.length == 1 && ( 
       args[0].equals("--help") ||
       args[0].equals("-h")
       )){
      help();
      return;
    }
    try{
      _bootstrapProps = new Properties(System.getProperties());
      if(args.length > 0){
        Utils.loadProps(RegistryServer.class, _bootstrapProps, args[0]);
      }
      
      if(_bootstrapProps.getProperty(Consts.IP_PATTERN_KEY) != null){
        System.setProperty(Consts.IP_PATTERN_KEY, _bootstrapProps.getProperty(Consts.IP_PATTERN_KEY));
      }
      
      RegisDebug.enabled = _bootstrapProps.getProperty(DEBUG, "true").equalsIgnoreCase("true");
      
      RegistryContext ctx = new RegistryContext(_bootstrapProps);
      Registry reg = ctx.connect();
      
      if(_bootstrapProps.getProperty(BOOTSTRAP) != null){
        try{
          loadBootStrap(reg, _bootstrapProps.getProperty(BOOTSTRAP), _bootstrapProps);
        }catch(Exception e){
          System.out.println("Could not load bootstrap configuration.");
          System.out.println("Got the following error:");
          e.printStackTrace();
          return;
        }
      }      

      _exporter = new RegistryExporter(
          _bootstrapProps.getProperty(USERNAME, DEFAULT_USERNAME), 
          _bootstrapProps.getProperty(PASSWORD, DEFAULT_PASSWORD), 
          reg, 
          _bootstrapProps);
      if(_bootstrapProps.getProperty(Consts.UBIK_DOMAIN_NAME) != null){
        _exporter.setDomain(_bootstrapProps.getProperty(Consts.UBIK_DOMAIN_NAME));
      }
      _exporter.setPeerToPeer(new Boolean(_bootstrapProps.getProperty(PEER_TO_PEER, "false")).booleanValue());
      if(_bootstrapProps.getProperty(SERVER_JNDI_NAME) != null){
        _exporter.bind(_bootstrapProps.getProperty(SERVER_JNDI_NAME), _bootstrapProps);
      }
      else if(_bootstrapProps.getProperty(SERVER_PORT) != null){
        _exporter.bind(Integer.parseInt(_bootstrapProps.getProperty(SERVER_PORT)));
      }
      else{
        throw new IllegalStateException("One of the following properties must be specified:" +
            SERVER_JNDI_NAME + " or " + SERVER_PORT);
      }
      System.out.println("Registry server started - typce CTRL-C to abort cleanly.");
      Runtime.getRuntime().addShutdownHook(new ShutdownHook());
      
      if(startThread){
        while(true){
          try{
            Thread.sleep(100000);
          }catch(InterruptedException e){
            e.printStackTrace();
            break;
          }
        }
      }
    }catch(Exception e){
      e.printStackTrace();
    }
  }
   
  static final void help(){
    System.out.println("Excepted argument: <propfile> ");
  }
  
  static void loadBootStrap(Registry reg, String bootstrap, Properties props) throws Exception{
    String[] resources = bootstrap.split(",");
    RegisSession session = null;
    try{
      session = reg.open();
      Node node = reg.getRoot();
      if(reg instanceof Configurable){
        loadConfigurable((Configurable)reg, resources);
      }
      else if(node instanceof Configurable){
        loadConfigurable((Configurable)node, resources);        
      }
      else{
        RWSession rw = (RWSession)session; 
        rw.begin();
        try{
          RegistryConfigLoader loader = new RegistryConfigLoader((RWNode)reg.getRoot());
          for(int i = 0; i < resources.length; i++){
            loader.load(Utils.load(RegistryServer.class, resources[i].trim()), props);
          }
          rw.commit();
        }catch(RuntimeException e){
          rw.rollback();
          throw e;
        }
      }
    }catch(ClassCastException e){
      throw new IllegalStateException("Registry does not support write operations; cannot update");
    }finally{
      if(session != null){
        session.close();
      }
    }
  }
  
  private static void loadConfigurable(Configurable conf, String[] resources) throws Exception{
    Path path = Path.parse(Node.ROOT_NAME);
    for(int i = 0; i < resources.length; i++){
      try {
        RegisLog.debug(RegistryServer.class, "Loading resource '" + resources[i].trim() + "'");
        String xmlConf = Utils.loadAsString(Utils.load(RegistryServer.class, resources[i].trim()));
        conf.load(path, null, null, xmlConf, _bootstrapProps);
      } catch (Exception e) {
        RegisLog.error(RegistryServer.class, "Error loading resource " + resources[i].trim() + " - " + e + ": " + e.getLocalizedMessage());
        throw e;
      }
    }
  }
  
  public static class ShutdownHook extends Thread{
    public void run() {
      try{
        Hub.shutdown(30000);
        if(_exporter != null)
          _exporter.close();
      }catch(InterruptedException e){
        e.printStackTrace();
      }
      System.out.println("Stopped registry server.");      
    }
  }

}
