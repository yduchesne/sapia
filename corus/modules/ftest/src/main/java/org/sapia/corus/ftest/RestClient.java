package org.sapia.corus.ftest;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.sapia.corus.client.ClusterInfo;
import org.sapia.corus.client.facade.CorusConnector;
import org.sapia.corus.client.facade.CorusConnectorBuilder;
import org.sapia.corus.client.services.security.Permission;
import org.sapia.ubik.net.TCPAddress;
import org.sapia.ubik.util.Collects;
import org.sapia.ubik.util.Localhost;

/**
 * Utility client-side class for executing REST functional tests.
 * 
 * @author yduchesne
 * 
 */
public class RestClient {

  public static final String HEADER_APP_ID  = "X-corus-app-id";
  public static final String HEADER_APP_KEY = "X-corus-app-key";
  
  public static final  String ROLE_ADMIN = "ftest-admin";
  
  private static final int DEFAULT_PORT  = 33000;
  
  private static final String APP_KEY      = UUID.randomUUID()
        .toString().toLowerCase().replace("-", "");
  private static final String APP_ID_ADMIN = ROLE_ADMIN + "-app";
  
  private static final AtomicInteger REF_COUNT = new AtomicInteger();
  
  private static RestClient instance;
  
  private CorusConnector connector;
  private Client         client;
  
  private RestClient(CorusConnector connector, Client client) {
    this.connector = connector;
    this.client    = client;
  }
  
  /**
   * @return the {@link CorusConnector}.
   */
  public CorusConnector getConnector() {
    if (connector == null) {
      try {
        init();
      } catch (Exception e) {
      }
    }
    return connector;
  }
  
  /**
   * @return the application key used for functional testing.
   */
  public String getAppkey() {
    return APP_KEY;
  }
  
  /**
   * @return the <code>admin</code> application ID.
   */
  public String getAdminAppId() {
    return APP_ID_ADMIN;
  }
  
  /**
   * Invoke in order to execute a HTTP PUT.
   *
   * @param path a resource path.
   * @return a new {@link WebTarget} instance.
   * @throws IOException if an I/O error occurs.
   */
  public WebTarget resource(String path) throws IOException {
    WebTarget target = client.target(UriBuilder.fromUri(url(path)));
    return target;
  }
  
  /**
   * @param path a resource path.
   * @return the URL string for the given path.
   */
  public String url(String path) {
    TCPAddress corusAddress = getConnector().getContext().getServerHost().getEndpoint().getServerTcpAddress();
    String url = "http://" + corusAddress.getHost() + ":" + corusAddress.getPort() + "/rest" 
        + (path.startsWith("/") ? path : "/" + path);
    return url;
  }
  
  /**
   * @return this class' singleton.
   */
  public static RestClient open() {
    if (instance == null) {
      try {
        init();
      } catch (Exception e) {
        throw new IllegalStateException("Could not set up connection", e);
      }
    }
    REF_COUNT.incrementAndGet();
    return instance;
  }
  
  /**
   * Shuts down this instance's resources.
   */
  public void close() {
    if (REF_COUNT.decrementAndGet() == 0) {
      instance.client.close();
    }
  }

  // --------------------------------------------------------------------------
  
  private static synchronized void init() throws Exception {
    if (instance == null) {
      CorusConnector connector = CorusConnectorBuilder.newInstance()
          .host(Localhost.getPreferredLocalAddress().getHostAddress())
          .port(DEFAULT_PORT)
          .basedir(new File("../package/target"))
          .build();
      
      connector.getSecurityManagementFacade()
        .addOrUpdateRole(ROLE_ADMIN, Collects.arrayToSet(Permission.values()), ClusterInfo.clustered());
      
      connector.getApplicationKeyManagementFacade().createApplicationKey(
          APP_ID_ADMIN, APP_KEY, ROLE_ADMIN, ClusterInfo.clustered()
      );
      
      ClientConfig conf = new ClientConfig();
      conf.register(JsonMessageBodyConverter.class);
      Client client = ClientBuilder.newClient(conf);
      instance = new RestClient(connector, client);
    }
  }

}
