package org.sapia.beeq.hibernate.queue.retry;

import java.util.Date;

import org.sapia.beeq.hibernate.queue.QueueElement;

public class RetryQueueElement extends QueueElement{
  
  private int retryCount;
  private Date nextRetryDate;

  public Date getNextRetryDate() {
    return nextRetryDate;
  }

  public void setNextRetryDate(Date nextRetryDate) {
    this.nextRetryDate = nextRetryDate;
  }  

  public int getRetryCount() {
    return retryCount;
  }

  public void setRetryCount(int retryCount) {
    this.retryCount = retryCount;
  }
  
  public String toString(){
    return "[status="+getStatus()+", nextRetryDate="+getNextRetryDate()+", retryCount="+getRetryCount()+"]";
  }

}
