package org.sapia.ubik.rmi.naming.remote;

import static org.junit.Assert.*;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.naming.InitialContext;

import junit.extensions.TestSetup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.sapia.ubik.concurrent.BlockingRef;
import org.sapia.ubik.log.Log;
import org.sapia.ubik.mcast.EventChannel;
import org.sapia.ubik.mcast.EventConsumer;
import org.sapia.ubik.mcast.memory.InMemoryBroadcastDispatcher;
import org.sapia.ubik.mcast.memory.InMemoryUnicastDispatcher;
import org.sapia.ubik.rmi.Consts;
import org.sapia.ubik.rmi.naming.remote.discovery.DiscoveryHelper;
import org.sapia.ubik.rmi.naming.remote.discovery.ServiceDiscoListener;
import org.sapia.ubik.rmi.naming.remote.discovery.ServiceDiscoveryEvent;
import org.sapia.ubik.rmi.server.Hub;
import org.sapia.ubik.util.PropUtil;

public class EmbeddableJNDIServerTest  {
  
  private EventChannel channel1, channel2;
  private EmbeddableJNDIServer jndi;
  
  @Before
  public void setUp() throws Exception {
    EventChannel.disableReuse();
    EventConsumer cons1 = new EventConsumer("test");
    channel1 = new EventChannel(
        cons1, 
        new InMemoryUnicastDispatcher(cons1), new InMemoryBroadcastDispatcher(cons1)
    );

    EventConsumer cons2 = new EventConsumer("test");
    channel2 = new EventChannel(
        cons2, 
        new InMemoryUnicastDispatcher(cons2), new InMemoryBroadcastDispatcher(cons2)
    );
    
    jndi = new EmbeddableJNDIServer(channel1, 1098);
  }
  
  @After
  public void tearDown() {
    EventChannel.enableReuse();
    channel1.close();
    channel2.close();
    PropUtil.clearUbikSystemProperties();
    Hub.shutdown();
  }

  @Test
  public void testInMemoryBindServiceDiscovery() throws Exception {
    jndi.start(true);
    
    DiscoveryHelper helper = new DiscoveryHelper(channel2.getReference());
    final BlockingRef<TestService> ref = new BlockingRef<>();
    helper.addServiceDiscoListener(new ServiceDiscoListener() {
      @Override
      public void onServiceDiscovered(ServiceDiscoveryEvent evt) {
        try {
          ref.set((TestService) evt.getService()); 
        } catch (RemoteException e) {
          ref.setNull();
        }
      }
    });
    channel2.start();
    
    jndi.getLocalContext().bind("test", Mockito.mock(TestService.class));
    
    TestService service = ref.await();
    
    assertNotNull(service);
  }
  
  @Test
  public void testInMemoryBindServiceDiscovery_LateStartOfJndi() throws Exception {
    
    DiscoveryHelper helper = new DiscoveryHelper(channel2.getReference());
    final BlockingRef<TestService> ref = new BlockingRef<>();
    helper.addServiceDiscoListener(new ServiceDiscoListener() {
      @Override
      public void onServiceDiscovered(ServiceDiscoveryEvent evt) {
        try {
          ref.set((TestService) evt.getService()); 
        } catch (RemoteException e) {
          ref.setNull();
        }
      }
    });
    channel2.start();
    
    jndi.start(true);
    jndi.getLocalContext().bind("test", Mockito.mock(TestService.class));
    
    TestService service = ref.await();
    
    assertNotNull(service);
  }
  
  @Test
  public void testInMemoryBindServiceDiscovery_LateStartOfDiscoHelper() throws Exception {
    DiscoveryHelper helper = new DiscoveryHelper(channel2.getReference());
    final BlockingRef<TestService> ref = new BlockingRef<>();
    helper.addServiceDiscoListener(new ServiceDiscoListener() {
      @Override
      public void onServiceDiscovered(ServiceDiscoveryEvent evt) {
        try {
          ref.set((TestService) evt.getService()); 
        } catch (RemoteException e) {
          ref.setNull();
        }
      }
    });
    
    jndi.start(true);
    jndi.getLocalContext().bind("test", Mockito.mock(TestService.class));
    channel2.start();
    
    TestService service = ref.await();
    
    assertNotNull(service);
  }
  
  @Test
  public void testRemoteLookup() throws Exception {
    jndi.start(true);
    jndi.getLocalContext().bind("test", Mockito.mock(TestService.class));

    Properties props = new Properties();
    props.setProperty(Consts.UNICAST_PROVIDER,  Consts.UNICAST_PROVIDER_MEMORY);
    props.setProperty(Consts.BROADCAST_PROVIDER,  Consts.BROADCAST_PROVIDER_MEMORY);

    props.setProperty(RemoteInitialContextFactory.UBIK_DOMAIN_NAME, "test");
    props.setProperty(InitialContext.PROVIDER_URL, "ubik://localhost:1111");
    props.setProperty(InitialContext.INITIAL_CONTEXT_FACTORY, RemoteInitialContextFactory.class.getName());

    InitialContext ctx = new InitialContext(props);
    TestService service =  (TestService) ctx.lookup("test");
  }
  
  public interface TestService {
    public void test();
  }

}
