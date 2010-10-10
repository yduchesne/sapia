package org.sapia.soto.xfire.sample;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public class WebServer {

  public static void main(String[] args) throws Exception{
    Server server = new Server(8191);
    WebAppContext ctx = new WebAppContext();
    ctx.setContextPath("/soap");
    ctx.setWar(System.getProperty("user.dir") + "/webapp");
    server.addHandler(ctx);
    ctx.setParentLoaderPriority(true);
    
    server.start();    
    
    Runtime.getRuntime().addShutdownHook(
      new ShutdownHook(server)  
    );
  }
  
  public static class ShutdownHook extends Thread{
    
    Server server;
    
    public ShutdownHook(Server server) {
      this.server = server;
    }
    
    public void run() {
      try {
        server.stop();
      } catch (Exception e) { }
    }
  }
}
