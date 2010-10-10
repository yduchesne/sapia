package org.sapia.soto.ubik.example;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import org.sapia.soto.SotoContainer;
import org.sapia.soto.ubik.monitor.Monitor;
import org.sapia.soto.ubik.monitor.StatusResult;
import org.sapia.soto.ubik.monitor.StatusResultList;

public class MonitorApp {
  
  public static void main(String[] args) throws Exception{
    
    SotoContainer soto = new SotoContainer();
    soto.load(new File("etc/ubik/monitor.server.xml"));
    soto.start();
    
    System.out.println("Started monitor...");
    
    while(true){
      try{
        Thread.sleep(1000);
      }catch(InterruptedException e){
        soto.dispose();
      }
      
        System.out.println("================ RUN =================");
      Monitor mon = (Monitor)soto.lookup(Monitor.class);
      StatusResultList results = mon.getStatusForClass(null);
      Iterator itr = results.getResults().iterator();
      while(itr.hasNext()){
        StatusResult res = (StatusResult)itr.next();
        System.out.println("--------------------------------------");
        System.out.println("Got status for " + 
            res.getServiceClassName() + ", " + 
            (res.getServiceId() != null ? res.getServiceId() : " ---"));      
        if(res.isError()){
          System.out.println("Status indicates an error:");
          res.getError().printStackTrace(System.out);
        }
        else{
          System.out.println("Status OK");
        }
        System.out.println();
        System.out.println("Status properties: ");      
        Properties props = res.getProperties();
        props.list(System.out);
      }
    }
  }  

}
