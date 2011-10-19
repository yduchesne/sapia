/**
 * 
 */
package org.sapia.soto.util.concurrent;

import java.sql.Timestamp;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class ThrottledExecutorService extends ExecutorService {

  /** Defines the maximum number of task executed per second. */
  private double _maxThroughputPerSecond;
  
  /** The calculated minimum time in millisecond between each tasks. */
  private long _minimumTaskDelayMillis;
  
  /** The timestamp of the last task executed. */
  private long _lastExecutionTimestamp;
  
  /** Defines wether throtlting occurs all the time or only when explicitly called for (default to false). */
  private boolean _isThrottlingExplicit;
  
  /** The internal scheduled executor of this service. */
  private ScheduledThreadPoolExecutor _scheduledExecutor;
  
  /**
   * Creates a new ThrottledExecutorService instance.
   */
  public ThrottledExecutorService() {
    _isThrottlingExplicit = false;
  }
  
  /**
   * Returns the maximum number of task executed per second.
   * 
   * @return The maximum number of task executed per second.
   */
  public double getMaxThroughputPerSecond() {
    return _maxThroughputPerSecond;
  }
  
  /**
   * Changes the maximum number of task executed per second. Setting this
   * value to zero (0) will have the effect of not performing any throttling
   * on the executed or submitted tasks.
   * 
   * @param aMaxThroughputPerSecond The maximum number of task executed per second.
   */
  public void setMaxThroughputPerSecond(double aMaxThroughputPerSecond) {
    _maxThroughputPerSecond = aMaxThroughputPerSecond;
    if (aMaxThroughputPerSecond > 0) {
      _minimumTaskDelayMillis = Math.round(1000.0 / aMaxThroughputPerSecond);
    } else {
      _minimumTaskDelayMillis = 0;
    }
  }
  
  /**
   * Returns true if throttling will be applied only when explicitly called by using one
   * of the method <code>executeThrottled()</code> or <code>submitThrottled()</code>.
   * Otherwise the throttling logic will always be applied.
   * 
   * @return True if throttling is applied only when calling throttling method.
   */
  public boolean isThrottlingExplicit() {
    return _isThrottlingExplicit;
  }
  
  /**
   * Changes the value of the explicit throttling application of this executor.
   * 
   * @param isThrottlingExplicit The new value.
   */
  public void setThrottlingExplicit(boolean isThrottlingExplicit) {
    _isThrottlingExplicit = isThrottlingExplicit;
  }
  
  /* (non-Javadoc)
   * @see org.sapia.soto.util.concurrent.ExecutorService#init()
   */
  public void init() throws Exception {
    super.init();
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.util.concurrent.ExecutorService#start()
   */
  public void start() throws Exception {
    super.start();
  }
  
  /* (non-Javadoc)
   * @see org.sapia.soto.util.concurrent.ExecutorService#dispose()
   */
  public void dispose() {
    super.dispose();
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.util.concurrent.ExecutorService#createExecutor(java.util.concurrent.ThreadFactory)
   */
  protected ThreadPoolExecutor createExecutor(ThreadFactory aThreadFactory) {
    _scheduledExecutor = new ScheduledThreadPoolExecutor(
            getCorePoolSize(), aThreadFactory, getRejectedExecutionHandler());
    
    return _scheduledExecutor;
  }
  
  /* (non-Javadoc)
   * @see org.sapia.soto.util.concurrent.ExecutorService#execute(java.lang.Runnable)
   */
  public void execute(Runnable aTask) {
    if (!isRunning()) {
      throw new IllegalArgumentException("Cannot execute a task - the executor service is not running");
    }
    
    if (_isThrottlingExplicit) {
      _scheduledExecutor.execute(aTask);
    } else {
      executeThrottled(aTask);
    }
  }
  
  /**
   * Executes the given command at some time in the future according to the throttling
   * parameters of this executor service. The command may execute in a new thread, in
   * a pooled thread, or in the calling thread, at the discretion of the
   * <tt>Executor</tt> implementation.
   *
   * @param aTask The runnable task to execute.
   * @throws IllegalStateException If this executor service is not running.
   * @throws NullPointerException If the task passed in is null.
   * @throws RejectedExecutionException If this task cannot be accepted for execution.
   */
  public void executeThrottled(Runnable aTask) {
    if (!isRunning()) {
      throw new IllegalArgumentException("Cannot execute a task - the executor service is not running");
    }
    
    long delayMillis = calculateThrottlingDelayMillis();
    if (delayMillis > 0) {
      _scheduledExecutor.schedule(aTask, delayMillis, TimeUnit.MILLISECONDS);
    } else {
      _scheduledExecutor.execute(aTask);
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.util.concurrent.ExecutorService#submit(java.util.concurrent.Callable)
   */
  public Future submit(Callable aTask) {
    if (!isRunning()) {
      throw new IllegalArgumentException("Cannot submit a task - the executor service is not running");
    }
    
    if (_isThrottlingExplicit) {
      return _scheduledExecutor.submit(aTask);
    } else {
      return submitThrottled(aTask);
    }
  }

  /**
   * Submits a Callable task for execution and returns a Future representing that task. The
   * execution of the callable will be delayed according to the throttling parameters of
   * this executor service.
   *
   * @param aTask The task to submit.
   * @return a Future representing pending completion of the task, and whose <tt>get()</tt> method
   *         will return <tt>null</tt> upon completion.
   * @throws IllegalStateException If this executor service is not running.
   * @throws NullPointerException If the task passed in is null.
   * @throws RejectedExecutionException If the task cannot be scheduled for execution.
   */
  public Future submitThrottled(Callable aTask) {
    if (!isRunning()) {
      throw new IllegalArgumentException("Cannot submit a task - the executor service is not running");
    }
    
    long delayMillis = calculateThrottlingDelayMillis();
    if (delayMillis > 0) {
      return _scheduledExecutor.schedule(aTask, delayMillis, TimeUnit.MILLISECONDS);
    } else {
      return _scheduledExecutor.submit(aTask);
    }
  }

  /**
   * Calculated the throttling delay of the next task.
   * 
   * @return The throttling delay to apply on the next executed task in milliseconds.
   */
  protected synchronized long calculateThrottlingDelayMillis() {
    long delayMillis = 0;

    if (_minimumTaskDelayMillis > 0) {
      long delta = System.currentTimeMillis() - _lastExecutionTimestamp;
      if (delta < _minimumTaskDelayMillis) {
        delayMillis = _minimumTaskDelayMillis - delta;
      }
    }

    _lastExecutionTimestamp = System.currentTimeMillis();
    return delayMillis;
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.ubik.monitor.FeedbackMonitorable#monitor()
   */
  public Properties monitor() throws Exception {
    Properties props = super.monitor();

    props.setProperty("executor.maxThroughputPerSecond", String.valueOf(_maxThroughputPerSecond));
    props.setProperty("executor.minimumTaskDelayMillis", String.valueOf(_minimumTaskDelayMillis));
    props.setProperty("executor.isThrottlingExplicit", String.valueOf(_isThrottlingExplicit));
    props.setProperty("executor.lastExecutionTimestamp", new Timestamp(_lastExecutionTimestamp).toString());
    
    return props;
  }
}
