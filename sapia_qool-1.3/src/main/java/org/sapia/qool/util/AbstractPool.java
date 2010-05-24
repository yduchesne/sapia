package org.sapia.qool.util;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.JMSException;

import org.sapia.qool.Debug;

public abstract class AbstractPool <T extends Poolable, C extends Object>{

  private AtomicInteger busyCount = new AtomicInteger(0);
  private AtomicBoolean closed = new AtomicBoolean(false);
  
  private LinkedList<T> available = new LinkedList<T>();

  private int maxSize;
  private long timeout;
  
  protected Debug debug;
  
  protected AbstractPool(){
    debug = Debug.createInstanceFor(this);
  }
  
  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }
  
  public long getTimeout() {
    return timeout;
  }
  
  public void setMaxSize(int maxSize) {
    this.maxSize = maxSize;
  }
  
  public int getMaxSize() {
    return maxSize;
  }
  
  public synchronized int getAvailableCount(){
    return available.size();
  }
  
  public int getBusyCount(){
    return busyCount.get();
  }
  
  public synchronized T acquire(C config) throws InterruptedException, JMSException{
    if(closed.get()){
      throw new IllegalStateException("Pool is closed");
    }
    if(maxSize > 0 && busyCount.get() >= maxSize){
      while(busyCount.get() >= maxSize){
        if(debug.isDebug()) debug.debug("waiting for object... (busy count: " + busyCount.get() + " - available: " + available.size());
        if(timeout > 0){
          wait(timeout);
          if(busyCount.get() >= maxSize){
            throw new JMSException("Could not acquire object within specified delay: " + timeout);
          }
        }
        else{
          wait();  
        }
      }
    }
    
    T pooled;
    
    if(available.size() == 0){
      if(debug.isDebug()) debug.debug("acquiring: creating new object (busy count: " + busyCount.get() + " - available: " + available.size());
      pooled = doCreate(config);
    }
    else{
      if(debug.isDebug()) debug.debug("acquiring: reusing object (busy count: " + busyCount.get() + " - available: " + available.size());
      pooled = available.removeFirst();
    }
    busyCount.incrementAndGet();
    return pooled;
  }
  
  public synchronized void release(T pooled) {
    if(closed.get()){
      throw new IllegalStateException("Pool is closed");
    }
  
    boolean isValid = pooled.recycle();

    if(isValid){
      this.available.addLast(pooled);
      if(debug.isDebug()) debug.debug("releasing: adding object back to pool (busy count: " + busyCount.get() + " - available: " + available.size());
    }  
    busyCount.decrementAndGet();
 
    notify();
  }
  
  public synchronized void close(){
    if(!this.closed.get()){
      this.closed.set(true);
      for(T t:this.available){
        try{
          t.dispose();
        }catch(Exception e){
          debug.warn("Error disposing pooled object: " + t, e);
        }
      }
    }
  }
  
  protected abstract T doCreate(C config) throws JMSException;
}

