package org.sapia.beeq.hibernate.queue;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.sapia.beeq.Message;
import org.sapia.beeq.MessageID;
import org.sapia.beeq.MessageStatus;
import org.sapia.beeq.hibernate.HibernateMessage;
import org.sapia.beeq.hibernate.NodeConfiguration;
import org.sapia.beeq.queue.Queue;
import org.sapia.beeq.queue.QueueListener;

public class HibernateQueue implements Queue{
  
  protected SessionFactory sessions;
  protected QueueListener listener;
  protected ExecutorService executor;
  protected int batchSize;
  protected String nodeId;
  protected Log log = LogFactory.getLog(getClass());
 
  public HibernateQueue(String nodeId, QueueListener listener, SessionFactory sessions, NodeConfiguration conf){
    this.sessions = sessions;
    this.listener = listener;
    this.executor = Executors.newFixedThreadPool(conf.getMaxThreads());
    this.batchSize = conf.getBatchSize();
    this.nodeId = nodeId;
    fireOnMessage();
  }
  
  public void close(){
    executor.shutdown();
    try{
      executor.awaitTermination(5, TimeUnit.SECONDS);
    }catch(InterruptedException e){
      //noop
    }
  }
  
  public MessageID add(Message message) {
    Session sess = sessions.openSession();
    Transaction tx = null;
    try{
      tx = sess.beginTransaction();
      QueueElement element = newElementFor(message);
      sess.save(element);
      if(log.isDebugEnabled()){
        log.debug("Adding message to queue: " + message);
      }      
      tx.commit();
    }catch(RuntimeException e){
      if(tx != null){
        tx.rollback();
      }
      throw e;
    }finally{
      sess.close();
    }
    fireOnMessage();
    return message.getID();
  }
  
  protected FutureTask<Void> fireOnMessage() {
    FutureTask<Void> task = new FutureTask<Void>(new OnMessageTrigger());
    executor.execute(task);
    return task;
  }
  
  protected QueueElement newElementFor(Message msg){
    QueueElement element = new QueueElement();
    element.setMessage(msg);
    return element;
  }
  
  protected synchronized List getQueueElements(Session s, int batchSize){
    Transaction tx = s.beginTransaction();
    try{
      List elements = s.createCriteria(QueueElement.class)
        .add(Restrictions.eq("status", QueueElementStatus.CREATED))
        .createCriteria("message").add(Restrictions.eq("nodeId", this.nodeId))
        .setFirstResult(0).setMaxResults(batchSize).list();
      
      for(int i = 0; i < elements.size(); i++){
        QueueElement elem = (QueueElement)elements.get(i);
        elem.setStatus(QueueElementStatus.PROCESSING);
      }
      tx.commit();
      return elements;
    }catch(RuntimeException e){
      tx.rollback();
      throw e;
    }
  }
  

  
  protected void handleRemove(QueueElement element, Session s, boolean succeeded){
    if(succeeded){
      if(log.isDebugEnabled()){
        log.debug("Processing of message " + element.getMessage().getID() + " succeeded");
      }
      ((HibernateMessage)element.getMessage()).setStatus(MessageStatus.SUCCEEDED);
    }
    else{
      if(log.isDebugEnabled()){
        log.debug("Processing of message " + element.getMessage().getID() + " failed; discarding");
      }      
      ((HibernateMessage)element.getMessage()).setStatus(MessageStatus.FAILED);
    }
    s.delete(element);    
  }
  
  class OnMessageTrigger implements Callable<Void>{
    
    public Void call() throws Exception {
      Session sess = sessions.openSession();
      try{
        doRun(sess);
      }finally{
        sess.close();
      }
      return null;
    }
    
    private void doRun(Session sess) throws Exception{
      List elements = null;
      do{
        try{
          elements = getQueueElements(sess, batchSize);
        }catch(RuntimeException e){
          log.error("Could not acquire pending elements", e);
          return;
        }
        if(log.isDebugEnabled()){
          log.debug("Got pending queue elements (" + elements.size() + ")");
        }        
        Transaction tx = null;
        try{
          tx = sess.beginTransaction();
          for(int i = 0; i < elements.size(); i++){
            QueueElement element = (QueueElement)elements.get(i);
            try{
              listener.onMessage(element.getMessage());
              handleRemove(element, sess, true);            
            }
            catch(Exception e){
              handleRemove(element, sess, false);
            }
          }
          tx.commit();
        }catch(RuntimeException e){
          log.error("Exception caught while processing pending elements", e);
          if(tx != null){
            tx.rollback();
          }
        }
      }while(elements == null || elements.size() > 0);
    }
  }
  

}
