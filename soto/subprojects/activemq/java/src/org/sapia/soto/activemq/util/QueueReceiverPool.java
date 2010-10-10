/**
 * 
 */
package org.sapia.soto.activemq.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.sapia.soto.ConfigurationException;
import org.sapia.soto.Service;
import org.sapia.soto.ubik.monitor.FeedbackMonitorable;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class QueueReceiverPool implements Service, FeedbackMonitorable {

  private int _ackMode = Session.AUTO_ACKNOWLEDGE;
  private boolean _transacted = false;
  private int _poolSize;
  
  private Queue _queue;
  private QueueConnection _connection;
  private QueueConnectionFactory _factory;
  private String _selectorString;
  private MessageListener _messageListener;
  private boolean _isRunning;
  private List _receivers;

  
  public QueueReceiverPool() {
    _receivers = new ArrayList();
    _isRunning = false;
  }
  
  /**
   * The default <code>Queue</code> to which this instance will send messages.
   * @param queue a <code>Queue</code>.
   */
  public void setQueue(Queue queue) {
    _queue = queue;
  }

  /**
   * @param fac the <code>QueueConnectionFactory</code> used by this instance to create
   * <code>QueueConnection</code>s.
   * 
   * @throws JMSException
   */
  public void setQueueConnectionFactory(QueueConnectionFactory fac) {
    _factory = fac;
  }

  /**
   * Changes the selection string of consumed messages on the JMS queue.
   * 
   * @param aSelector The new selector string.
   */
  public void setSelectorString(String aSelector) {
    _selectorString = aSelector;
  }
  
  /**
   * Changes the message listener of this receiver pool.
   * 
   * @param aListener The new message listener.
   * @throws JMSException If an error occurs assigning the message listener.
   */
  public void setMessageListener(MessageListener aListener) throws JMSException {
    _messageListener = aListener;
    
    if (_isRunning) {
      synchronized (_receivers) {
        for (Iterator it = _receivers.iterator(); it.hasNext(); ) {
          QueueReceiverRef ref = (QueueReceiverRef) it.next();
          ref.setDelegateListener(aListener);
        }
      }
    }
  }
  
  /**
   * @param tx <code>transacted</code>
   */
  public void setTransacted(boolean tx) {
    _transacted = tx;
  }
  
  /**
   * @param ackMode sets the JMS message acknowledgement mode of this instance. 
   * Defaults to <code>Session.AUTO_ACKNOWLEDGE</code>.
   */
  public void setAckMode(int ackMode) {
    _ackMode = ackMode;
  }  
  
  /**
   * Changes the size of this receiver pool.
   * 
   * @param aSize The new size.
   */
  public void setPoolSize(int aSize) {
    _poolSize = aSize;
    
    if (_isRunning) {
      try {
        adjustPoolSize();
      } catch (JMSException jmse) {
        System.err.println("Error adjusting pool to new size");
        jmse.printStackTrace();
      }
    }
  }
  
  /* (non-Javadoc)
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
    if (_factory == null) {
      throw new ConfigurationException("Queue connection factory is not set");
    } else if (_queue == null) {
      throw new ConfigurationException("Queue is not set on this receiver pool");
    }
    
    _connection = _factory.createQueueConnection();
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.Service#start()
   */
  public synchronized void start() throws Exception {
    _connection.start();
    adjustPoolSize();
    _isRunning = true;
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.Service#dispose()
   */
  public synchronized void dispose() {
    _isRunning = false;
    
    try {
      _poolSize = 0;
      adjustPoolSize();
    } catch (JMSException jmse) {
      System.err.println("Error closing queue receiver");
      jmse.printStackTrace();
    } finally {
      _receivers.clear();
    }
    
    try {
      _connection.stop();
      _connection.close();
    } catch (JMSException jmse) {
      System.err.println("Error closing queue connection");
      jmse.printStackTrace();
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.ubik.monitor.FeedbackMonitorable#monitor()
   */
  public Properties monitor() throws Exception {
    long messageCount = 0;
    for (Iterator it = new ArrayList(_receivers).iterator(); it.hasNext(); ) {
      QueueReceiverRef ref = (QueueReceiverRef) it.next();
      messageCount += ref.getMessageCount();
    }

    Properties props = new Properties();
    props.setProperty("isRunning", Boolean.toString(_isRunning));
    props.setProperty("activeReceiverCount", Integer.toString(_receivers.size()));
    props.setProperty("messageReceivedCount", Long.toString(messageCount));
    return props;
  }

  /**
   * Dynamically changes the content of the pool according to the define pool size.
   * 
   * @throws JMSException If an error occurs in the operation.
   */
  protected void adjustPoolSize() throws JMSException {
    synchronized (_receivers) {
      while (_receivers.size() != _poolSize) {
        if (_receivers.size() < _poolSize) {
          // Creating missing entry
          QueueSession session = _connection.createQueueSession(_transacted, _ackMode);
          QueueReceiver receiver;
          if (_selectorString != null && _selectorString.length() > 0) {
            receiver = session.createReceiver(_queue, _selectorString);
          } else {
            receiver = session.createReceiver(_queue);
          }
          _receivers.add(new QueueReceiverRef(receiver, session, _messageListener));
         
        } else {
          // Removing extra entry
          QueueReceiverRef ref = (QueueReceiverRef) _receivers.remove(_receivers.size()-1);
          ref.close();
        }
      }
    }
  }
  
}
