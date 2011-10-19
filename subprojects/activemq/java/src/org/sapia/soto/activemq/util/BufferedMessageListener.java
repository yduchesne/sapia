/**
 * 
 */
package org.sapia.soto.activemq.util;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 *
 * @author Jean-Cedric Desrochers
 */
public class BufferedMessageListener implements MessageListener {

  private int _processingDelayMillis = 0;
  private int messageCount;
  private List _messages;
  
  public BufferedMessageListener() {
    this(0);
  }
  
  public BufferedMessageListener(int processingDelayMillis) {
    if (processingDelayMillis > 0) {
      _processingDelayMillis = processingDelayMillis;
    }
    _messages = new ArrayList();
  }
  
  /* (non-Javadoc)
   * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
   */
  public void onMessage(Message aMessage) {
    synchronized (_messages) {
      _messages.add(aMessage);
      messageCount++;
      _messages.notify();
    }
    try {
//      System.err.println("==> [" + Thread.currentThread().getName() + "] received text message:" + ((TextMessage) aMessage).getText());
      Thread.sleep(_processingDelayMillis);
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }

  /**
   * Returs the next available message.
   * 
   * @return The next available message or null.
   */
  public Message getNextMessage() {
    Message result = null;
    
    synchronized (_messages) {
      if (_messages.size() > 0) {
        result = (Message) _messages.remove(0);
      }
    }
    
    return result;
  }
  
  public Message awaitNextMessage(long aTimeoutMillis) throws InterruptedException {
    Message result = null;
    
    synchronized (_messages) {
      if (_messages.size() == 0) {
        _messages.wait(aTimeoutMillis);
        if (_messages.size() > 0) {
          result = (Message) _messages.remove(0);
        }
        
      } else {
        result = (Message) _messages.remove(0);
      }
    }
    
    return result;
  }
}
