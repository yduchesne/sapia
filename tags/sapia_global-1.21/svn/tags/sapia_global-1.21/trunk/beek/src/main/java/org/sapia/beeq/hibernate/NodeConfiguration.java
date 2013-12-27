package org.sapia.beeq.hibernate;

import org.sapia.beeq.hibernate.queue.retry.RatioRetryPolicy;
import org.sapia.beeq.hibernate.queue.retry.RetryPolicy;


/**
 * An instance of this class encapsulates configuration for an
 * {@link HibernateNode} instance.
 *  
 * @author yduchesne
 *
 */
public class NodeConfiguration {
  
  private int maxThreads = 1;
  private int batchSize = 50;
  private int maxRetry = 3;
  private RetryPolicy retryPolicy;
  
  public NodeConfiguration(){
    this.setRetryPolicy(new RatioRetryPolicy());
  }
  
  /**
   * @return the size of the batches of queued messages that will
   * be processed at a time.
   */
  public int getBatchSize() {
    return batchSize;
  }
  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }
  
  /**
   * @return the maximum number of threads that will be used to
   * perform processing of the queued messages.
   */
  public int getMaxThreads() {
    return maxThreads;
  }
  public void setMaxThreads(int maxThreads) {
    this.maxThreads = maxThreads;
  }
  
  public int getMaxRetry() {
    return maxRetry;
  }
  public void setMaxRetry(int maxRetry) {
    this.maxRetry = maxRetry;
  }

  public RetryPolicy getRetryPolicy() {
    return retryPolicy;
  }

  public void setRetryPolicy(RetryPolicy retryPolicy) {
    this.retryPolicy = retryPolicy;
  }

}
