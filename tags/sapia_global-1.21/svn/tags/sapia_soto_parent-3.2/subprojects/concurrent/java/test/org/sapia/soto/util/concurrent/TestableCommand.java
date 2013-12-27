package org.sapia.soto.util.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * Test command for unit tests.
 * 
 */
public class TestableCommand {

  private long _creationTime;
  private long _executionTime;
  private Exception _cause;
  private CountDownLatch _completionLatch;
  
  public TestableCommand() {
    _creationTime = System.currentTimeMillis();
    _completionLatch = new CountDownLatch(1);
  }
  
  public void setCause(Exception aCause) {
    _cause = aCause;
  }
  
  public boolean isCompleted() {
    return _executionTime > 0;
  }
  
  public void waitForCompletion(long aTimeout) throws InterruptedException {
    _completionLatch.await(aTimeout, TimeUnit.MILLISECONDS);
  }
  
  public long getDelayBeforeExecution() {
    return _executionTime - _creationTime; 
  }
  
  public void execute() throws Exception {
    _executionTime = System.currentTimeMillis();
    _completionLatch.countDown();
    
    if (_cause == null) {
      return;
    } else {
      throw _cause;
    }
  }
}
