package org.sapia.soto.activemq.util;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.sapia.soto.Service;
import org.sapia.soto.activemq.MessageListenerFactory;

/**
 * This class is a utility that internally creates JMS {@link javax.jms.QueueReceiver}s,
 * registering each of them with a {@link javax.jms.MessageListener} created by a given
 * {@link org.sapia.soto.activemq.MessageListenerFactory}. 
 * <p>
 * An instance of this class must be configured with the following (see the corresponding 
 * setter methods):
 * 
 * <ul>
 *   <li>a JMS {@link javax.jms.Queue}, for which messages should be processed.
 *   <li>a JMS {@link javax.jms.QueueConnectionFactory}, to create a connection on the appropriate queue.
 *   <li>a {@link org.sapia.soto.activemq.MessageListenerFactory}, which creates the {@link javax.jms.MessageListener}
 *   implementations that are meant to process the incoming messages.
 * </ul>
 * 
 * <p>
 * Optionally, the following may be set:
 * 
 * <ul>
 *   <li>the number of {@link javax.jms.QueueReceivers} that are internally created and registered with a 
 *   MessageListener (defaults to 1).
 *   <li>the <code>transacted</code> flag (internally used when creating {@link QueueSession}s) - defaults to false.
 *   <li>the ack mode (corresponding to one of the JMS ack modes, as specified in the {@link javax.jms.Session}
 *   interface (defaults to Session.AUT_ACKNOWLEDGE).
 * </ul>
 * 
 * @see org.sapia.soto.activemq.util.QueueSenderPool
 * 
 * @author yduchesne
 *
 */
public class QueueMessageProcessor implements Service{

  private int _ackMode = Session.AUTO_ACKNOWLEDGE;
  private boolean _transacted = false;
    
  private Queue _queue;
  private QueueConnection _connection;
  private QueueConnectionFactory _fac;
  private int _numReceivers = 1;
  private List _receivers; 
  private MessageListenerFactory _listenerFac;

  public void setQueue(Queue queue){
    _queue = queue;
  }
  
  public void setQueueConnectionFactory(QueueConnectionFactory fac) throws JMSException{
    _fac = fac;
  }

  /**
   * @param numReceivers the number of <code>QueueReceiver</code>s that will be internally
   * kept.
   */
  public void setNumReceivers(int numReceivers){
    if(_numReceivers > 1){
      _numReceivers = numReceivers;
    }
  }
  
  /**
   * 
   * @param listenerFactory the <code>MessageListenerFactory</code> that will be 
   * used by this instance to create <code>MessageListener</code>s that will
   * handle incoming messages.
   */
  public void setMessageListenerFactory(MessageListenerFactory listenerFactory){
    _listenerFac = listenerFactory;
  }  
  
  /**
   * @param tx <code>transacted</code>
   */
  public void setTransacted(boolean tx){
    _transacted = tx;
  }
  
  /**
   * @param ackMode sets the JMS message acknowledgement mode of this instance. 
   * Defaults to <code>Session.AUTO_ACKNOWLEDGE</code>.
   */
  public void setAckMode(int ackMode){
    _ackMode = ackMode;
  }    
  
  public void init() throws Exception {
    if(_fac == null){
      throw new IllegalStateException("QueueConnectionFactory not set");
    }
    if(_queue == null){
      throw new IllegalStateException("Queue not set");
    }        
    if(_fac == null){
      throw new IllegalStateException("MessageListenerFactory not set");
    }    
    _connection = _fac.createQueueConnection();
  }
  
  public void start() throws Exception {
    _connection.start();
    _receivers = new ArrayList(_numReceivers);
    for(int i = 0; i < _numReceivers; i++){
      QueueSession session = _connection.createQueueSession(_transacted, _ackMode);
      QueueReceiver receiver = session.createReceiver(_queue);
      QueueReceiverRef ref = new QueueReceiverRef(receiver, session, _listenerFac.createMessageListener());
      _receivers.add(ref);
    }
    
  }
  
  public void dispose() {
    for(int i = 0; i < _receivers.size(); i++){
      QueueReceiverRef ref = (QueueReceiverRef)_receivers.get(i);
      try{
        ref.getSession().close();
      }catch(Exception e){
        //noop
      }      
      try{
        ref.getReceiver().close();
      }catch(Exception e){
        //noop
      }
    }
  }
  
}
