package org.sapia.beeq.hibernate.queue.retry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.sapia.beeq.Message;
import org.sapia.beeq.MessageStatus;
import org.sapia.beeq.hibernate.HibernateMessage;
import org.sapia.beeq.hibernate.NodeConfiguration;
import org.sapia.beeq.hibernate.queue.HibernateQueue;
import org.sapia.beeq.hibernate.queue.QueueElement;
import org.sapia.beeq.hibernate.queue.QueueElementStatus;
import org.sapia.beeq.queue.QueueListener;

/**
 * This queue inherits from the {@link HibernateQueue} class and implements
 * retry behavior: messages are processed a given amount of time (and at a given
 * interval), and discarded as soon as processing as succeeded, or if the maximum
 * number of retries has been reached.
 * 
 * @author yduchesne
 */
public class HibernateRetryQueue extends HibernateQueue{
  
  private RetryPolicy policy;
  private int maxRetry  = 3;
  private Thread retryThread;
  
  private static final long DEFAULT_INTERVAL = 10000;
  
  public HibernateRetryQueue(String nodeId, QueueListener listener, SessionFactory sessions, NodeConfiguration conf){
    super(nodeId, listener, sessions, conf);
    policy = conf.getRetryPolicy();
    maxRetry = conf.getMaxRetry();
    retryThread = new Thread(new RetryRunnable(), getClass().getName()+"-thread");
    retryThread.setDaemon(true);
    retryThread.start();
  }
  
  public void setMaxRetry(int max){
    maxRetry = max;
  }
  
  public void close() {
    super.close();
    int attempt = 0;
    int maxAttempt = 3;
    if(retryThread != null && attempt < maxAttempt){
      log.warn("Interrupting message processing");      
      while(retryThread.isAlive()){
        retryThread.interrupt();
        try{
          retryThread.join(3333);
        }catch(InterruptedException e){
          break;
        }
        maxAttempt++;
      }
    }
    log.warn("Message processing stopped");
  }
  
  @Override
  protected QueueElement newElementFor(Message msg) {
    RetryQueueElement elem = new RetryQueueElement();
    ((HibernateMessage)msg).setStatus(MessageStatus.PROCESSING);
    elem.setMessage(msg);
    elem.setStatus(QueueElementStatus.PENDING);
    elem.setNextRetryDate(policy.calculateNextRetry(new Date(), 1));
    return elem;
  }
  
  @Override
  protected List getQueueElements(Session s, int batchSize) {
    Transaction tx = s.beginTransaction();

    Date current = new Date();
    try{
      
      // selecting pending elements
      List elements =   s.createCriteria(RetryQueueElement.class)
        .add(Restrictions.eq("status", QueueElementStatus.PENDING))
        .add(Restrictions.le("nextRetryDate", current))
        .createCriteria("message").add(Restrictions.eq("nodeId", this.nodeId))
        .setFirstResult(0).setMaxResults(batchSize).list();
      
      // selecting pending elements, flagging them as being processed
      // (deleting those for which max retry has been reached
      List<RetryQueueElement> toReturn = new ArrayList<RetryQueueElement>(elements.size());
      for(int i = 0; i < elements.size(); i++){
        RetryQueueElement elem = (RetryQueueElement)elements.get(i);
        if(elem.getRetryCount() < maxRetry){
          elem.setStatus(QueueElementStatus.PROCESSING);          
          toReturn.add(elem);
        }
        else{
          if(log.isDebugEnabled()){
            log.debug("Max retry reached for message " + elem.getMessage().getID() + " - removing from retry queue...");
          }
          ((HibernateMessage)elem.getMessage()).setStatus(MessageStatus.FAILED);
          s.delete(elem);
        }
      }
      tx.commit();
      return toReturn;
    }catch(RuntimeException e){
      tx.rollback();
      throw e;
    }
  }
  
  @Override
  protected void handleRemove(QueueElement elem, Session s, boolean succeeded) {
    RetryQueueElement retry = (RetryQueueElement)elem;
    if(retry.getRetryCount() >= maxRetry || succeeded){
      if(succeeded){
        if(log.isDebugEnabled()){
          log.debug("Message successfully processed: " + elem.getMessage().getID());
        }
        ((HibernateMessage)elem.getMessage()).setStatus(MessageStatus.SUCCEEDED);
      }
      else{
        if(log.isDebugEnabled()){
          log.debug("Message processing failed - max retry reached: " + elem.getMessage().getID());
        }        
        ((HibernateMessage)elem.getMessage()).setStatus(MessageStatus.FAILED);
      }      
      s.delete(elem);
    }
    else{
      retry.setStatus(QueueElementStatus.PENDING);
      retry.setRetryCount(retry.getRetryCount()+1);
      retry.setNextRetryDate(policy.calculateNextRetry(new Date(), retry.getRetryCount()));      
      if(log.isDebugEnabled()){
        log.debug("Message processing failed - attempting retry: " + elem.getMessage().getID() + " - " + elem + "; next schedule: " + retry.getNextRetryDate());
      }      
    }
  }
  
  //////////// INNER CLASSES
  
  class RetryRunnable implements Runnable{
    
    public void run() {
      while(true){
        try{
          fireOnMessage().get();
        }catch(RuntimeException e){
          log.error("Caught runtime exception while processing messages", e);
        }catch(ExecutionException e){
          log.warn("Caught execution exception, while performing asynchronous processing", e);
        }catch(InterruptedException e){
          log.warn("Interrupted, stopping message processing");
          break;          
        }
        try{
          long interval = computeInterval();
          log.warn("Sleeping: " + interval);
          Thread.sleep(interval);
        }
        catch(InterruptedException e){
          log.warn("Interrupted, stopping message processing");
          break;
        }        
      }
    }
    
    long computeInterval(){
      Session s = sessions.openSession();
      try{
        List results = s.createQuery("select min(qe.nextRetryDate) as nextRetry from " + RetryQueueElement.class.getName() + " qe").list();
        if(results.size() == 0){
          return DEFAULT_INTERVAL;
        }
        else{
          Date next    = (Date)results.get(0);
          if(next == null){
            return DEFAULT_INTERVAL;
          }
          long interval = next.getTime() - System.currentTimeMillis();
          if(interval < 0){
            return 0;
          }
          else{
            return interval;
          }
        }
      }catch(RuntimeException e){
        e.printStackTrace();
        log.error(e);
        return DEFAULT_INTERVAL;
      }finally{
        s.close();
      }
    }
  }

}
