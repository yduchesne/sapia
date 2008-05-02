package org.sapia.beeq.hibernate.queue.retry;

import java.util.Calendar;
import java.util.Date;

/**
 * Calculates the next retry date given on a "base" interval (defaults to 3 minutes) 
 * and a growth factor (defaults to 1.5). The following formula is used to calculated
 * the next retry date:
 * <pre>
 *  next retry date = intertval + interval * (retry count * growth factor). 
 * </pre> 
 *  
 * @author yduchesne
 *
 */
public class RatioRetryPolicy implements RetryPolicy{
  
  long interval;
  float growthFactor;
  
  public RatioRetryPolicy(){
    setIntervalMinutes(3);
    setGrowthFactor(1.5f);
  }
  
  public void setIntervalMillis(long interval){
    this.interval = interval;
  }
  
  public void setIntervalSeconds(int secs){
    setIntervalMillis(secs*1000);
  }
  
  public void setIntervalMinutes(int mins){
    setIntervalSeconds(mins*60);
  }
  
  public void setGrowthFactor(float factor){
    this.growthFactor = factor;
  }
  
  public Date calculateNextRetry(Date lastRetry, int retryCount) {
    Calendar cal = Calendar.getInstance();
    if(lastRetry == null){
      lastRetry = new Date();
    }
    long diff = interval + interval * (long)(retryCount * growthFactor);
    cal.setTimeInMillis(lastRetry.getTime() + diff);
    return cal.getTime();
  }
}
