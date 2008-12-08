package org.sapia.regis.prof;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.RegisDebug;
import org.sapia.regis.Registry;
import org.sapia.regis.RegistryContext;
import org.sapia.ubik.rmi.Consts;
import org.sapia.ubik.rmi.server.Log;
import org.sapia.util.unit.TimeUnit;

public class Client {

  public static void main(String[] args) throws Exception{
    
    int max = 1;
    
    List<Thread> workers = new ArrayList<Thread>(max);
    
    //Log.setInfo();
    System.setProperty(Consts.CLIENT_GC_THRESHOLD, "500");
    System.setProperty(Consts.CLIENT_GC_BATCHSIZE, "1500");
    System.setProperty(Consts.CLIENT_GC_INTERVAL,  "5000");
    System.setProperty(Consts.STATS_ENABLED,  "true");    
    System.setProperty(Consts.JMX_ENABLED,  "true");

    //RegisDebug.enabled = true;
    
    long time = TimeUnit.HOUR.convertAsLong(12, TimeUnit.MILLIS);
    System.out.println("Running time: " + time);
    
    for(int i = 0; i < max; i++){
      
      workers.add(new Thread(new Worker(time, getNode()), "worker-" + i));
    }
    
    for(Thread w: workers){
      w.start();
    }
    
  }
  
  private static Node getNode() throws Exception{
    Properties props = new Properties();
    props.setProperty("registry.address", "localhost");
    props.setProperty(RegistryContext.BOOTSTRAP, "etc/profiling/client.properties");
    RegistryContext ctx = new RegistryContext(props);
    Registry reg = ctx.connect();
    //RegisSession s = reg.open();
    Node node = reg.getRoot();
    Node child = node.getChild("test");
    child = child.getChild("testapp1");
    Node propNode = child.getChild("backend1").getChild("properties");
    return propNode;
  }
 
 
  
  
  public static class Worker implements Runnable{
    
    long runningTime;
    Node propNode;
    
    Worker(long runningTime, Node propNode){
      this.runningTime = runningTime;
      this.propNode = propNode;
    }
    
    public void run() {
      try {
        doRun();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    private void doRun() throws Exception{
      Collection propKeys = propNode.getPropertyKeys();
      long startTime = System.currentTimeMillis();
      while(System.currentTimeMillis() < startTime+runningTime){
        Iterator itr = propKeys.iterator();     
        while(itr.hasNext()){
          String key = (String)itr.next();
          propNode.renderProperty(key);
        }
        Thread.sleep(5000);
        /*
        Thread.yield();*/
      }  
      System.out.println("finished " + Thread.currentThread().getName());
    }
    
  }
}
