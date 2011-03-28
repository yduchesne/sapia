package org.sapia.soto.ubik.monitor.server;

import org.apache.log4j.BasicConfigurator;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public class JettyStarter {

  public static void main(String[] args) throws Exception{
    System.setProperty("corus.server.domain", "default");
    System.setProperty("DEBUG", "true");
    System.setProperty("soto.debug", "true");    
    System.setProperty("monitor.test", "true");
    BasicConfigurator.configure();
    Server server = new Server(8191);
    WebAppContext ctx = new WebAppContext();
    
    ctx.setParentLoaderPriority(true);
    ctx.setContextPath("/monitor");
    ctx.setWar(System.getProperty("user.dir") + "/webapp");
    server.addHandler(ctx);    
    server.start();
    while(true){
      try{
        Thread.sleep(100000);
      }catch(InterruptedException e){
        server.destroy();
      }
    }
  }
}
