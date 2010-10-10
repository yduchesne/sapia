package org.sapia.soto.corus.jmx.example;

import org.sapia.soto.SotoContainer;


public class CorusJmxServiceEg {

  public static void main(String[] args){
    try{
      System.setProperty("corus.server.port", "33000");
      System.setProperty("corus.server.host", "192.168.0.101");      
      System.setProperty("corus.process.id", "123456");
      SotoContainer container = new SotoContainer();
      container.load(new java.io.File("etc/corus/jmxSample.xml"));
      container.start();
      while(true){
        Thread.sleep(1000000);
      }
        
        
    }catch(Throwable t){
      t.printStackTrace();
    }
  }
}
