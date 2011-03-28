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
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.sapia.soto.ConfigurationException;
import org.sapia.soto.Service;
import org.sapia.soto.ubik.monitor.FeedbackMonitorable;

/**
 *
 * @author Jean-Cedric Desrochers
 */
public class TopicSubscriberPool implements Service, FeedbackMonitorable {

  private int _ackMode = Session.AUTO_ACKNOWLEDGE;
  private boolean _transacted = false;
  private int _poolSize;
  
  private Topic _topic;
  private TopicConnection _connection;
  private TopicConnectionFactory _factory;
  private String _selectorString;
  private MessageListener _messageListener;
  private boolean _isRunning;
  private List _subscribers;

  
  public TopicSubscriberPool() {
    _subscribers = new ArrayList();
    _isRunning = false;
  }
  
  /**
   * The default <code>Topic</code> to which this instance will send messages.
   * @param topic a <code>Topic</code>.
   */
  public void setTopic(Topic aTopic) {
    _topic = aTopic;
  }

  /**
   * @param fac the <code>TopicConnectionFactory</code> used by this instance to create
   * <code>TopicConnection</code>s.
   */
  public void setTopicConnectionFactory(TopicConnectionFactory fac) {
    _factory = fac;
  }

  /**
   * Changes the selection string of consumed messages on the JMS topic.
   * 
   * @param aSelector The new selector string.
   */
  public void setSelectorString(String aSelector) {
    _selectorString = aSelector;
  }
  
  /**
   * Changes the message listener of this subscriber pool.
   * 
   * @param aListener The new message listener.
   * @throws JMSException If an error occurs assigning the message listener.
   */
  public void setMessageListener(MessageListener aListener) throws JMSException {
    _messageListener = aListener;
    
    if (_isRunning) {
      synchronized (_subscribers) {
        for (Iterator it = _subscribers.iterator(); it.hasNext(); ) {
          TopicSubscriberRef ref = (TopicSubscriberRef) it.next();
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
      throw new ConfigurationException("Topic connection factory is not set");
    } else if (_topic == null) {
      throw new ConfigurationException("Topic is not set on this subscriber pool");
    }
    
    _connection = _factory.createTopicConnection();
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
      System.err.println("Error closing topic subscriber");
      jmse.printStackTrace();
    } finally {
      _subscribers.clear();
    }
    
    try {
      _connection.stop();
      _connection.close();
    } catch (JMSException jmse) {
      System.err.println("Error closing topic connection");
      jmse.printStackTrace();
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.ubik.monitor.FeedbackMonitorable#monitor()
   */
  public Properties monitor() throws Exception {
    long messageCount = 0;
    for (Iterator it = new ArrayList(_subscribers).iterator(); it.hasNext(); ) {
      TopicSubscriberRef ref = (TopicSubscriberRef) it.next();
      messageCount += ref.getMessageCount();
    }

    Properties props = new Properties();
    props.setProperty("isRunning", Boolean.toString(_isRunning));
    props.setProperty("activeSubscriberCount", Integer.toString(_subscribers.size()));
    props.setProperty("messageReceivedCount", Long.toString(messageCount));
    return props;
  }

  /**
   * Dynamically changes the content of the pool according to the define pool size.
   * 
   * @throws JMSException If an error occurs in the operation.
   */
  protected void adjustPoolSize() throws JMSException {
    synchronized (_subscribers) {
      while (_subscribers.size() != _poolSize) {
        if (_subscribers.size() < _poolSize) {
          // Creating missing entry
          TopicSession session = _connection.createTopicSession(_transacted, _ackMode);
          TopicSubscriber subscriber;
          if (_selectorString != null && _selectorString.length() > 0) {
            subscriber = session.createSubscriber(_topic, _selectorString, true);
          } else {
            subscriber = session.createSubscriber(_topic);
          }
          _subscribers.add(new TopicSubscriberRef(subscriber, session, _messageListener));
         
        } else {
          // Removing extra entry
          TopicSubscriberRef ref = (TopicSubscriberRef) _subscribers.remove(_subscribers.size()-1);
          ref.close();
        }
      }
    }
  }
}
