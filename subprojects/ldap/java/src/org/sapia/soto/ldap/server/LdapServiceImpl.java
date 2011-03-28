package org.sapia.soto.ldap.server;

import java.io.File;
import java.util.HashSet;

import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.entry.ServerEntry;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmIndex;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.server.xdbm.Index;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.sapia.soto.Service;
import org.sapia.soto.SotoContainer;

/**
 * This class embeds an ApacheDS LDAP server instance. The class extends <a
 * href="http://jsourcery.com/api/apache/directory/apacheds/1.0.1/org/apache/directory/server/configuration/MutableServerStartupConfiguration.html"
 * >MutableServerStartupConfiguration</a>, which allows calling this instance's
 * methods in an IOC fashion prior to the server being started - this instance
 * will be the server's configuration.
 * <p>
 * Some interesting attributes to set on this instance:
 * <ul>
 * <li><b>workingDirectory</b>: the directory where this instance will keep its
 * data. The contents of that directory should be backed up.
 * <li><b>adminPassword</b>: the administrator password (will be set to "secret"
 * by default).
 * <li><b>ldapPort</b>: the port on which the LDAP server will listen (defaults
 * to 10389).
 * </ul>
 * 
 * You can log into the server using a LDAP client tool. The administrator
 * username is "admin", and the organizational unit of the administrator is
 * "system". See the ApacheDS <a href="http://directory.apache.org/apacheds/1.0/221-connecting-to-apacheds-with-graphical-tools-3rd-party.html"
 * >docs</a> for more info.
 * 
 * @author yduchesne
 * 
 */
public class LdapServiceImpl implements LdapService, Service {

  public static final int DEFAULT_PORT = 10389;

  public static final String DEFAULT_ADMIN_PWD = "secret";

  private String _adminPwd = DEFAULT_ADMIN_PWD;

  private String _baseDN;

  private DirectoryService service;

  private LdapServer server;

  private File workingDirectory = new File("target/ldap");

  public LdapServiceImpl() {

  }

  public void setAdminPassword(String pwd) { _adminPwd = pwd; }
 
  public void setBaseDN(String dn) { _baseDN = dn; }
  
  public void setWorkingDirectory(String dir) { 
    this.workingDirectory = new File(dir);
  }


  public void init() throws Exception {
    

    // Initialize the LDAP service
    service = new DefaultDirectoryService();

    // Disable the ChangeLog system
    service.getChangeLog().setEnabled(false);


    // And start the service
    service.startup();

    service.setShutdownHookEnabled( true );
    service.setAccessControlEnabled(false);
    service.setAllowAnonymousAccess(false); 

    server = new LdapServer();
    server.setDirectoryService( service );
    server.setAllowAnonymousAccess( true );

    // Set LDAP port to 10389
    TcpTransport ldapTransport = new TcpTransport( 10389 );
    server.setTransports( ldapTransport );

    // Determine an appropriate working directory
    this.workingDirectory.mkdirs();
    service.setWorkingDirectory(this.workingDirectory);
    service.startup();
    server.start();
 

  }

  public void start() throws Exception {

  }

  public void dispose() {
    /*
     * Hashtable env = new Hashtable(); if (_baseDN != null) {
     * env.put(Context.PROVIDER_URL, _baseDN); }
     * env.put(Context.INITIAL_CONTEXT_FACTORY,
     * "org.apache.directory.server.jndi.ServerContextFactory"); env.putAll(new
     * ShutdownConfiguration().toJndiEnvironment());
     * env.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
     * env.put(Context.SECURITY_CREDENTIALS, _adminPwd); // This is
     * bug-or-wierdness workaround for in-VM access to the // DirContext of
     * ApacheDS env.put( Configuration.JNDI_KEY, new SyncConfiguration()); try {
     * Context ctx = new InitialDirContext(env); ctx.close(); } catch (Exception
     * e) { }
     */
  }

  public static void main(String[] args) throws Throwable {
    SotoContainer cont = new SotoContainer();
    cont = new SotoContainer();
    cont.load(new File("etc/server.soto.xml"));
    cont.start();
    while (true) {
      Thread.sleep(100000);
    }
  }

}
