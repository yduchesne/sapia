package org.sapia.corus.taskmanager.v2;

import java.lang.reflect.InvocationTargetException;

/**
 * 
 * @author yduchesne
 *
 */
public class FutureResult {
  
  private boolean completed = false;
  private Object result;

  public synchronized Object get() throws InterruptedException, InvocationTargetException{
    return doGet(0);
  }

  public synchronized Object get(long timeout) throws InterruptedException, InvocationTargetException{
    return doGet(timeout);
  }

  private synchronized Object doGet(long timeout) throws InterruptedException, InvocationTargetException{
    while(!completed) wait(timeout);
    if(result != null){
      if(result instanceof InvocationTargetException){
        throw (InvocationTargetException)result;
      } 
      else{
        return result;
      }
    }
    else{
      return null;
    }
  }
  
  synchronized void completed(Object result){
    if(result != null && result instanceof Throwable){
      this.result = new InvocationTargetException((Throwable)result);
    }
    else{
      this.result = result;
    }
    completed = true;
    notifyAll();
  }
  
  
}
