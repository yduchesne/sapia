package org.sapia.soto.ubik.example;

import java.io.File;

import org.sapia.soto.SotoContainer;

public class MonitorAgentApp {
  
  public static void main(String[] args) throws Exception{
    
    SotoContainer soto = new SotoContainer();
    soto.load(new File("etc/ubik/monitor.client.xml"));
    try{
      soto.start();
      while(true){
        Thread.sleep(1000000);
      }
    }catch(InterruptedException e){
      soto.dispose();
    }
  }

}
