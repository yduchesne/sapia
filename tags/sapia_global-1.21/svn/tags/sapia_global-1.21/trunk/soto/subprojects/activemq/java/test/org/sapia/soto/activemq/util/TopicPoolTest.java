/**
 * 
 */
package org.sapia.soto.activemq.util;

import java.io.File;

import javax.jms.Message;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.sapia.soto.SotoContainer;

/**
 *
 * @author Jean-C�dric Desrochers
 */
public class TopicPoolTest extends TestCase {

  static {
    BasicConfigurator.configure();
  }
  
  private SotoContainer _soto;
  private TopicSubscriberPool _subscriberPool;
  private TopicPublisherPool _publisherPool;
  private BufferedMessageListener _messageListener;
  
  public void setUp() throws Exception {
    _soto = new SotoContainer();
    _soto.load(new File("etc/activemq/util/topicPoolTest.soto.xml"));
    _soto.start();
    
    _messageListener = new BufferedMessageListener(150);
    _subscriberPool = (TopicSubscriberPool) _soto.lookup("subscriberPool");
    _subscriberPool.setMessageListener(_messageListener);
    _publisherPool = (TopicPublisherPool) _soto.lookup("publisherPool");
  }
  
  public void tearDown() throws Exception {
    _subscriberPool.setMessageListener(null);
    _soto.dispose();
  }
  
  public void testSendingAndReceiving() throws Exception {
    int TOTAL_MESSAGE_COUNT = 40;
    int NUMBER_THREADS = 4;
    PublisherTask task = new PublisherTask(TOTAL_MESSAGE_COUNT/NUMBER_THREADS);
    for (int i = 1; i <= NUMBER_THREADS; i++) {
      new Thread(task, getName() + "-thread" + i).start();
    }
    
    int messageReceived = 0;
    boolean isDone = false;
    while (!isDone) {
      Message message = _messageListener.awaitNextMessage(1500);
      if (message != null) {
        messageReceived++;
//        System.err.println("==> processing text message:" + ((TextMessage) message).getText());
      } else {
        isDone = true;
      }
    }
   
    assertEquals("The number of received message is invalid", TOTAL_MESSAGE_COUNT, messageReceived);
  }
  
  public class PublisherTask implements Runnable {
    private int _messageCount;
    public PublisherTask(int messageCount) {
      _messageCount = messageCount;
    }
    public void run() {
      try {
        for (int i = 1; i <= _messageCount; i++) {
          _publisherPool.sendTextMessage(Thread.currentThread().getName() + "-" + i);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
