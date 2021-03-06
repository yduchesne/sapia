package org.sapia.ubik.rmi.examples.jndi;

import java.lang.reflect.Proxy;

import javax.naming.Context;
import javax.naming.NameNotFoundException;

import org.sapia.ubik.log.IncludeClassFilter;
import org.sapia.ubik.log.Log;
import org.sapia.ubik.mcast.EventChannel;
import org.sapia.ubik.rmi.examples.Foo;
import org.sapia.ubik.rmi.naming.remote.EmbeddableJNDIServer;
import org.sapia.ubik.rmi.naming.remote.JndiSyncTask;
import org.sapia.ubik.rmi.naming.remote.LazyStubInvocationHandler;
import org.sapia.ubik.rmi.naming.remote.discovery.DiscoveryHelper;
import org.sapia.ubik.rmi.naming.remote.discovery.JndiDiscoListener;
import org.sapia.ubik.rmi.naming.remote.discovery.ServiceDiscoListener;
import org.sapia.ubik.rmi.naming.remote.discovery.ServiceDiscoveryEvent;
import org.sapia.ubik.rmi.server.gc.CommandGc;
import org.sapia.ubik.rmi.server.stub.StatelessStubTable;
import org.sapia.ubik.util.Func;

public class EmbeddedJndi2 {

  public static void main(String[] args) throws Exception {
    Log.setDebug();
    Log.setLogFilter(IncludeClassFilter.newInstance(EmbeddableJNDIServer.class, StatelessStubTable.class, JndiSyncTask.class, CommandGc.class));

    final EventChannel channel = new EventChannel("default");
    final EmbeddableJNDIServer server = new EmbeddableJNDIServer(channel.getReference(), 1099);
    final DiscoveryHelper discoHelper = new DiscoveryHelper(channel.getReference());
    discoHelper.addJndiDiscoListener(new JndiDiscoListener() {
      @Override
      public void onJndiDiscovered(Context ctx) {
        System.out.println("Discovered JNDI context");
      }
    });

    discoHelper.addServiceDiscoListener(new ServiceDiscoListener() {
      @Override
      public void onServiceDiscovered(ServiceDiscoveryEvent evt) {
        System.out.println("Discovered service: " + evt);
      }
    });

    channel.start();
    server.start(true);

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        server.stop();
        channel.close();
      }
    });

    //Thread.sleep(10000);

    Foo foo;
    try {
      foo = (Foo) server.getLocalContext().lookup("/my/services/foo");
    } catch (NameNotFoundException e) {
      LazyStubInvocationHandler handler = LazyStubInvocationHandler.Builder.newInstance()
        .context(server.getRemoteContext())
        .name("/my/services/foo")
        .matchFunction(new Func<Void, LazyStubInvocationHandler>() {
          @Override
          public Void call(LazyStubInvocationHandler handler) {
            discoHelper.removeServiceDiscoListener(handler);
            return null;
          }
        })
        .build();
      discoHelper.addServiceDiscoListener(handler);
      foo = (Foo) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] {Foo.class}, handler);
    }
    System.out.println("Started");
    
    
    
    
    while (true) {
      try {
        System.out.println(foo.getBar());
        System.out.println("Present");
      } catch (Exception e) {
        System.out.println("Not yet present");
      }
      Thread.sleep(60000);
    }
  }
}
