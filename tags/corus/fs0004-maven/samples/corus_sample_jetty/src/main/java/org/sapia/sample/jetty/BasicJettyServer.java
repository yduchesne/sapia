package org.sapia.sample.jetty;

import org.eclipse.jetty.server.Server;

public class BasicJettyServer {

  public static void main(String[] args) throws Exception{
    Server server = new Server(8080);
    server.setHandler(ServerUtil.loadWebapps());
    server.start();
    server.setStopAtShutdown(true);
    server.join();
    
    System.out.println("...Jetty server started successfully...");
  }
}
