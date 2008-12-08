package org.sapia.regis.prof;

import org.sapia.regis.RegisDebug;
import org.sapia.regis.remote.ProfilingInterceptor;
import org.sapia.regis.remote.RegistryServer;
import org.sapia.ubik.rmi.Consts;
import org.sapia.ubik.rmi.interceptor.Interceptor;
import org.sapia.ubik.rmi.server.Hub;
import org.sapia.ubik.rmi.server.gc.GcEvent;
import org.sapia.ubik.rmi.server.invocation.ServerPreInvokeEvent;

public class Server {

  
  public static void main(String[] args) throws Exception{
    //Log.setInfo();
    RegisDebug.enabled = true;
    System.setProperty(Consts.IP_PATTERN_KEY, "localhost");
    System.setProperty(Consts.SERVER_RESET_INTERVAL, "5000");
    System.setProperty(Consts.JMX_ENABLED, "true");
    RegistryServer.startThread = false;
    RegistryServer.main(new String[]{"etc/profiling/server.properties"});
    Interceptor it = new ProfilingInterceptor();
    Hub.serverRuntime.addInterceptor(ServerPreInvokeEvent.class, it);
    Hub.serverRuntime.addInterceptor(GcEvent.class, it);
    while(true){
      Thread.sleep(10000);
    }
  }
}
