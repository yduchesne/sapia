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
public class QueuePoolTest extends TestCase {

  static {
    BasicConfigurator.configure();
  }
  
  private SotoContainer _soto;
  private QueueReceiverPool _receiverPool;
  private QueueSenderPool _senderPool;
  private BufferedMessageListener _messageListener;
  
  public void setUp() throws Exception {
    _soto = new SotoContainer();
    _soto.load(new File("etc/activemq/util/queuePoolTest.soto.xml"));
    _soto.start();
    
    _messageListener = new BufferedMessageListener(150);
    _receiverPool = (QueueReceiverPool) _soto.lookup("receiverPool");
    _receiverPool.setMessageListener(_messageListener);
    _senderPool = (QueueSenderPool) _soto.lookup("senderPool");
  }
  
  public void tearDown() throws Exception {
    _receiverPool.setMessageListener(null);
    _soto.dispose();
  }
  
  public void testSendingAndReceiving() throws Exception {
    int TOTAL_MESSAGE_COUNT = 100;
    int NUMBER_THREADS = 4;
    SenderTask task = new SenderTask(TOTAL_MESSAGE_COUNT/NUMBER_THREADS);
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
  
  public class SenderTask implements Runnable {
    private int _messageCount;
    public SenderTask(int messageCount) {
      _messageCount = messageCount;
    }
    public void run() {
      try {
        for (int i = 1; i <= _messageCount; i++) {
          _senderPool.sendTextMessage(Thread.currentThread().getName() + "-" + i);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
