package org.sapia.corus.sample.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.session.SessionHandler;

public class EmbeddedServer {

  private int port;
  private SessionManager sessionManager;
  
  public EmbeddedServer(int port) {
    this(port, null);
  }
  
  public EmbeddedServer(int port, SessionManager sessionManager) {
    this.port = port;
    this.sessionManager = sessionManager;
  }
  
  public void start() throws Exception{
    Server server = new Server(port);
    server.setStopAtShutdown(true);
    if(sessionManager != null){
      SessionHandler sessionHandler = new SessionHandler(sessionManager);
      server.setHandler(ServerUtil.loadWebapps(sessionHandler));
    }
    else{
      server.setHandler(ServerUtil.loadWebapps());
    }
    server.start();
    System.out.println("*** Jetty server started successfully ***");
    server.join();
    System.out.println("*** Jetty server stopped ***");
  }
}
