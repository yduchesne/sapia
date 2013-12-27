/**
 * 
 */
package org.sapia.soto.activemq.cluster;

import java.io.File;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.sapia.soto.SotoContainer;

/**
 *
 * @author Jean-C�dric Desrochers
 */
public class ClusterCreationTest extends TestCase {

  static {
    BasicConfigurator.configure();
  }
  private SotoContainer _soto;
  private TestableClusterPeer _peerA, _peerB, _peerC;
  
  
  public void setUp() throws Exception {
    _soto = new SotoContainer();
    System.setProperty("soto.debug", "true");
    _soto.load(new File("etc/activemq/cluster/clusterCreationTest.soto.xml"));
    _soto.start();
    
    _peerA = (TestableClusterPeer) _soto.lookup("peerA");
    _peerB = (TestableClusterPeer) _soto.lookup("peerB");
    _peerC = (TestableClusterPeer) _soto.lookup("peerC");
  }
  
  public void tearDown() {
    _soto.dispose();
  }
  
  public void testSendingSingleMessage() throws Exception {
    _peerA.sendMessage("Hello Cluster");
    assertEquals("The received message is invalid", "peerA: Hello Cluster", _peerB.awaitMessage(2000));
    assertEquals("The received message is invalid", "peerA: Hello Cluster", _peerC.awaitMessage(2000));
    assertNull("The peer A should ne have a message", _peerA.awaitMessage(500));
  }
  
  public void testSendingMultipleMessages() throws Exception {
    _peerB.sendMessage("Reply to Cluster");
    _peerC.sendMessage("Reply to Cluster");

    assertEquals("The received message is invalid", "peerB: Reply to Cluster", _peerA.awaitMessage(2000));
    assertEquals("The received message is invalid", "peerC: Reply to Cluster", _peerA.awaitMessage(2000));
    
    assertEquals("The received message is invalid", "peerC: Reply to Cluster", _peerB.awaitMessage(2000));
    assertNull("The peer B should ne have a message", _peerB.awaitMessage(500));
    
    assertEquals("The received message is invalid", "peerB: Reply to Cluster", _peerC.awaitMessage(2000));
    assertNull("The peer C should ne have a message", _peerC.awaitMessage(500));
  }
}
