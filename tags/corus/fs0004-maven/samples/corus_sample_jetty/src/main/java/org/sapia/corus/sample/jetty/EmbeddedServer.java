package org.sapia.corus.sample.jetty;

import org.eclipse.jetty.server.Server;

public class EmbeddedServer {

  private int port;
  
  public EmbeddedServer(int port) {
    this.port = port;
  }
  
  public void start() throws Exception{
    Server server = new Server(port);
    server.setStopAtShutdown(true);
    server.setHandler(ServerUtil.loadWebapps());
    server.start();
    System.out.println("*** Jetty server started successfully ***");
    server.join();
    System.out.println("*** Jetty server stopped ***");
  }
}
