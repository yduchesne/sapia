package org.sapia.corus.sample.jetty;

import org.eclipse.jetty.server.Server;

public class BasicJettyServer {

  public static void main(String[] args) throws Exception{
    Server server = new Server(8080);
    server.setStopAtShutdown(true);
    server.setHandler(ServerUtil.loadWebapps());
    server.start();
    System.out.println("*** Jetty server started successfully ***");
    server.join();
    System.out.println("*** Jetty server stopped ***");

    
  }
}
