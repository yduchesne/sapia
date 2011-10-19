package org.sapia.soto.jgroups;

import java.io.File;

import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.sapia.soto.SotoContainer;

import junit.framework.TestCase;
;

/**
 * 
 *
 * @author Jean-Cedric Desrochers
 */
public class ChannelFactoryServiceImplTest extends TestCase {
  
  public static final String DEFAULT_PROTOCOL_STACK =
    "  UDP(mcast_addr=228.1.2.3;mcast_port=45566;ip_ttl=32):\n\n" +
    "  PING(timeout=100;num_initial_members=1):\n" +
    "  FD(timeout=3000):\n" +
    "  VERIFY_SUSPECT(timeout=1500):\n" +
    "  pbcast.NAKACK(gc_lag=10;retransmit_timeout=600,1200,2400,4800):\n" +
    "  UNICAST(timeout=600,1200,2400,4800):\n" +
    "  pbcast.STABLE(desired_avg_gossip=10000):\n" +
    "  FRAG:\n" +
    "  pbcast.GMS(join_timeout=5000;join_retry_timeout=2000;\n" +
    "             shun=true;print_local_addr=true)\n";

  private ChannelFactoryServiceImpl _service;
  private ProtocolStackDef _protocolStackDef;
  
  public void setUp() throws Exception {
    _protocolStackDef = new ProtocolStackDef();
    _protocolStackDef.setType("junit");
    _protocolStackDef.setProtocolStackString(DEFAULT_PROTOCOL_STACK);
    
    _service = new ChannelFactoryServiceImpl();
    _service.addProtocolStack(_protocolStackDef);
    _service.init();
    _service.start();
  }
  
  public void tearDown() {
    _service.dispose();
  }
  
  public void testCreateChannel_NullType() throws Exception {
    try {
      _service.createChannel(null);
      fail();
    } catch (ChannelException expected) {
    }
  }
  
  public void testCreateChannel_InvalidType() throws Exception {
    try {
      _service.createChannel("this-is-an-invalid-jgroup-type");
      fail();
    } catch (ChannelException expected) {
    }
  }
  
  /*
  public void testCreateChannel_ValidType() throws Exception {
    Channel channel = _service.createChannel("junit");
    channel.connect(getName());
    assertTrue("the created channel should be connected", channel.isConnected());
    channel.close();
  }
  
  
  public void testSotoContainer() throws Exception {
    SotoContainer soto = new SotoContainer();
    try {
      soto.load(new File("etc/jgroups/main.soto.xml"));
      soto.start();
      ChannelFactoryService service = (ChannelFactoryService) soto.lookup(ChannelFactoryService.class);

      try {
        service.createChannel("this-is-an-invalid-jgroup-type");
        fail();
      } catch (ChannelException expected) {
      }

      Channel channel = service.createChannel("test");
      channel.connect(getName());
      assertTrue("the created channel should be connected", channel.isConnected());
      channel.close();
      
    } finally {
      soto.dispose();
    }
    
  }
  */
}
