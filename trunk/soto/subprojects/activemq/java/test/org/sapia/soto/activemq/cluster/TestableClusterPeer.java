/**
 * 
 */
package org.sapia.soto.activemq.cluster;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.activecluster.Cluster;
import org.apache.activecluster.ClusterEvent;
import org.apache.activecluster.ClusterListener;
import org.sapia.soto.ConfigurationException;
import org.sapia.soto.Service;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class TestableClusterPeer implements Service, MessageListener, ClusterListener {

  private String _name;
  private Cluster _cluster;
  private MessageConsumer _consumer;
  private List _pendingMessages = new ArrayList();;
  
  /**
   * Changes the active cluster of this peer.
   * 
   * @param aCluster The new active cluster.
   */
  public void setCluster(Cluster aCluster) {
    _cluster = aCluster;
  }
  
  public void setName(String aName) {
    _name = aName;
  }
  
  /* (non-Javadoc)
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
    if (_cluster == null) {
      throw new ConfigurationException("The active cluster is not set");
    } else if (_name == null) {
      throw new ConfigurationException("The peer name is not set");
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {
    _consumer = _cluster.createConsumer(_cluster.getDestination());
    _consumer.setMessageListener(this);
    _cluster.start();
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
    try {
      _consumer.setMessageListener(null);
      _consumer.close();
      _cluster.stop();
    } catch (JMSException jmse) {
      jmse.printStackTrace();
    }
  }

  public String getMessage() {
    synchronized (_pendingMessages) {
      if (_pendingMessages.isEmpty()) {
        return null;
      } else {
        return (String) _pendingMessages.remove(0);
      }
    }
  }
  
  public String awaitMessage(long aTimeoutMillis) throws InterruptedException {
    synchronized (_pendingMessages) {
      if (_pendingMessages.isEmpty()) {
        _pendingMessages.wait(aTimeoutMillis);
        if (_pendingMessages.isEmpty()) {
          return null;
        } else {
          return (String) _pendingMessages.remove(0);
        }
      } else {
        return (String) _pendingMessages.remove(0);
      }
    }
  }
  
  /**
   * Sends a message to other peers
   * 
   * @param aText
   */
  public void sendMessage(String aText) {
    try {
      String[] messageBody = new String[2];
      messageBody[0] = _cluster.getLocalNode().getName();
      messageBody[1] = _name + ": " + aText;
      ObjectMessage jmsMessage = _cluster.createObjectMessage(messageBody);
      _cluster.send(_cluster.getDestination(), jmsMessage);
      
    } catch (JMSException jmse) {
      jmse.printStackTrace();
    }
  }
  
  /* (non-Javadoc)
   * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
   */
  public void onMessage(Message aJmsMessage) {
    if (aJmsMessage instanceof ObjectMessage) {
      try {
        synchronized (_pendingMessages) {
          String[] messageBody = (String[]) ((ObjectMessage) aJmsMessage).getObject();
          if (!messageBody[0].equals(_cluster.getLocalNode().getName())) {
            _pendingMessages.add(messageBody[1]);
            _pendingMessages.notify();
          }
        }
      } catch (JMSException jmse) {
        jmse.printStackTrace();
      }
    }
  }
  
  /* (non-Javadoc)
   * @see org.apache.activecluster.ClusterListener#onCoordinatorChanged(org.apache.activecluster.ClusterEvent)
   */
  public void onCoordinatorChanged(ClusterEvent anEvent) {
    log("onCoordinatorChanged(ClusterEvent) " + anEvent);
  }
  /* (non-Javadoc)
   * @see org.apache.activecluster.ClusterListener#onNodeAdd(org.apache.activecluster.ClusterEvent)
   */
  public void onNodeAdd(ClusterEvent anEvent) {
    log("onNodeAdd(ClusterEvent) " + anEvent);
  }
  /* (non-Javadoc)
   * @see org.apache.activecluster.ClusterListener#onNodeFailed(org.apache.activecluster.ClusterEvent)
   */
  public void onNodeFailed(ClusterEvent anEvent) {
    log("onNodeFailed(ClusterEvent) " + anEvent);
  }
  /* (non-Javadoc)
   * @see org.apache.activecluster.ClusterListener#onNodeRemoved(org.apache.activecluster.ClusterEvent)
   */
  public void onNodeRemoved(ClusterEvent anEvent) {
    log("onNodeRemoved(ClusterEvent) " + anEvent);
  }
  /* (non-Javadoc)
   * @see org.apache.activecluster.ClusterListener#onNodeUpdate(org.apache.activecluster.ClusterEvent)
   */
  public void onNodeUpdate(ClusterEvent anEvent) {
    log("onNodeUpdate(ClusterEvent) " + anEvent);
  }
  
  public void log(String aMessage) {
    System.err.println("==> " + _name + ": " + aMessage);
  }
}
