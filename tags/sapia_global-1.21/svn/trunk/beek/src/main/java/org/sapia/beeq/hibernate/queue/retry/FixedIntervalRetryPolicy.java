package org.sapia.beeq.hibernate.queue.retry;

import java.util.Calendar;
import java.util.Date;

/**
 * A retry policy that calculates the next retry date given a fixed
 * interval (which defaults to 3 minutes).
 * 
 * @author yduchesne
 *
 */
public class FixedIntervalRetryPolicy implements RetryPolicy{
  
  private long retryInterval;
 
  /**
   * @param retryInterval a time interval, in millis.
   */
  public FixedIntervalRetryPolicy(){
    setIntervalMinutes(3);
  }
  
  public void setIntervalMillis(long interval){
    this.retryInterval = interval;
  }
  
  public void setIntervalSeconds(int secs){
    setIntervalMillis(secs*1000);
  }
  
  public void setIntervalMinutes(int mins){
    setIntervalSeconds(mins*60);
  }  
  
  public Date calculateNextRetry(Date lastRetry, int retryCount) {
    Calendar cal = Calendar.getInstance();
    if(lastRetry == null){
      lastRetry = new Date();
    }
    cal.setTimeInMillis(lastRetry.getTime() + retryInterval);
    return cal.getTime();
  }
  
}
