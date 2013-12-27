package org.sapia.soto.util.concurrent;

import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sapia.soto.ConfigurationException;
import org.sapia.soto.Service;
//import org.sapia.soto.ubik.monitor.FeedbackMonitorable;

/**
 * This Soto service implements an executor service were runnable tasks can be asynchronously
 * executed. If a control is required to now when a runnable task is completed, users of this
 * executor service can <i>submit</i> their task and get a {@link java.util.concurrent.Future}
 * object to track the task execution.<P>
 * 
 * <B>This class requires the new JDK 1.5 utility concurrent package</B>
 *
 *   
 * @author Jean-Cedric Desrochers
 * <dl>
 *   <dt><b>Copyright: </b>
 *     <dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>.
 *         All Rights Reserved.</dd>
 *   </dt>
 *   <dt><b>License: </b>
 *     <dd>Read the license.txt file of the jar or visit the <a href="http://www.sapia-oss.org/license.html">license page</a>
 *         at the Sapia OSS web site</dd>
 *   </dt>
 * </dl>
 */
public class ExecutorService implements Service, Executor /*, FeedbackMonitorable*/ {

  /** Defines the default keep alive time of idle threads. */
  public static final long DEFAULT_KEEP_ALIVE = 60000;
  
  /** The logger of this executor service. */
  private Log _logger;
  
  /** The name of this executor service. */
  private String _name;
  
  /** The core size of the thread pool. */
  private int _coreThreadPoolSize;
  
  /** The maximum size of the thread pool. */
  private int _maximumThreadPoolSize;
  
  /** The keep alive time of idle extra threads (over core size) in milliseconds. */ 
  private long _threadKeepAliveTime;
  
  /** The size of the queue keeping scheduled tasks when all threads are busy. */
  private int _taskQueueSize;
  
  /** The handler of rejected execution of this executor service. */
  private RejectedExecutionHandler _rejectedExecutionHandler;
  
  /** The internal executor of this service. */
  private ThreadPoolExecutor _executor;
  
  /** Indicates if this executor service is running or not. */
  private boolean _isRunning;
  
  /**
   * Creates a new ExecutorService instance.
   *
   */
  public ExecutorService() {
    _logger = LogFactory.getLog(ExecutorService.class);
    _threadKeepAliveTime = DEFAULT_KEEP_ALIVE;
    _rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
  }
  
  /**
   * Returns the name of this executor service.
   * 
   * @return The name of this executor service.
   */
  public String getName() {
    return _name;
  }
  
  /**
   * Changes the name of this executor service to the value passed in.
   * 
   * @param aName The new name of this executor service.
   */
  public void setName(String aName) {
    _name = aName;
  }
  
  /**
   * Changes the logger of this executor to the one passed in.
   * 
   * @param aLogger The new logger instance.
   */
  public void setLogger(Log aLogger) {
    _logger = aLogger;
  }
  
  public int getCorePoolSize() {
    return _coreThreadPoolSize;
  }
  
  /**
   * Changes the core size of the thread pool to the value passed in.
   * 
   * @param aSize The new core size of the thread pool.
   */
  public void setCorePoolSize(int aSize) {
    _coreThreadPoolSize = aSize;
    if (_isRunning) {
      _executor.setCorePoolSize(aSize);
    }
  }
  
  public int getMaximumPoolSize() {
    return _maximumThreadPoolSize;
  }
  
  /**
   * Changes the maximum size of the thread pool to the value passed in.
   * 
   * @param aSize The new maximum size of the thread pool.
   */
  public void setMaximumPoolSize(int aSize) {
    _maximumThreadPoolSize = aSize;
    if (_isRunning) {
      _executor.setMaximumPoolSize(aSize);
    }
  }
  
  public long getThreadKeepAliveTime() {
    return _threadKeepAliveTime;
  }
  
  /**
   * Changes the keep alive time of idle extra threads (over core size).
   * The value of the time is in milliseconds.
   * 
   * @param aTime The new keep alive time.
   */
  public void setThreadKeepAliveTime(long aTime) {
    _threadKeepAliveTime = aTime;
    if (_isRunning) {
      _executor.setKeepAliveTime(aTime, TimeUnit.MILLISECONDS);
    }

  }
  
  public int getTaskQueueSize() {
    return _taskQueueSize;
  }
  
  /**
   * Changes the size of the queue holding pending tasks when all threads are busy.
   * 
   * @param aSize The new task queue size.
   */
  public void setTaskQueueSize(int aSize) {
    if (_isRunning) {
      throw new IllegalStateException("Cannot change the size of the task queue - the executor is running");
    } else {
      _taskQueueSize = aSize;
    }
  }
  
  public RejectedExecutionHandler getRejectedExecutionHandler() {
    return _rejectedExecutionHandler;
  }
  
  public void setRejectedExecutionHandler(RejectedExecutionHandler anExecutionHandler) {
    _rejectedExecutionHandler = anExecutionHandler;
  }
  
  /* (non-Javadoc)
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
    if (_name == null) {
      throw new ConfigurationException("The name of this executor service is not configured");
    } else if (_coreThreadPoolSize == 0) {
      throw new ConfigurationException("The thread pool core size of this executor service is not configured");
    }
    
    // Create the thread factory
    NamedThreadFactory threadFactory = new NamedThreadFactory(_name);
    
    // Normalize max pool size if not set
    if (_maximumThreadPoolSize == 0) {
      _maximumThreadPoolSize = _coreThreadPoolSize;
    }
    
    // Create the executor
    _executor = createExecutor(threadFactory);
  }
  
  /**
   * Internal factory method that creates the executor instance. Subclass may override this method to
   * change the actual executor implementation used.
   *
   * @param aThreadFactory The thread factory to use for the executor.
   * @return The created thead pool executor instance.
   */
  protected ThreadPoolExecutor createExecutor(ThreadFactory aThreadFactory) {
    // Create the queue of pending tasks
    BlockingQueue queue;
    if (_taskQueueSize == 0) {
      queue = new SynchronousQueue();
    } else {
      queue = new ArrayBlockingQueue(_taskQueueSize);
    }

    return new ThreadPoolExecutor(_coreThreadPoolSize, _maximumThreadPoolSize, _threadKeepAliveTime,
            TimeUnit.MILLISECONDS, queue, aThreadFactory, _rejectedExecutionHandler);
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {
    _isRunning = true;
    _logger.info("Started the '" + _name + "' executor service");
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
    _isRunning = false;
    try {
      _logger.info("Shutting down the '" + _name + "' executor service");

      _executor.shutdown();
      if (!_executor.awaitTermination(30, TimeUnit.SECONDS)) {
        _logger.warn("The '" + _name + "' executor service is not terminated, forcing shutdown" );
        _executor.shutdownNow();
      }
      
    } catch (InterruptedException ie) {
      _logger.warn("Thread interrupted awaiting termination of the executor service '" + _name + "', forcing shutdown");
      _executor.shutdownNow();
      
    } finally {
      _logger.info("The '" + _name + "' executor service is now stopped");
    }
  }

  /**
   * Returns true if this executor is running, false otherwise.
   * 
   * @return True if this executor is running.
   */
  public boolean isRunning() {
    return _isRunning;
  }
  
  /**
   * Executes the given command at some time in the future.  The command may execute in
   * a new thread, in a pooled thread, or in the calling thread, at the discretion of
   * the <tt>Executor</tt> implementation.
   *
   * @param aTask The runnable task to execute.
   * @throws IllegalStateException If this executor service is not running.
   * @throws NullPointerException If the task passed in is null.
   * @throws RejectedExecutionException If this task cannot be accepted for execution.
   */
  public void execute(Runnable aTask) {
    if (!_isRunning) {
      throw new IllegalArgumentException("Cannot execute a task - the executor service is not running");
    }
    
    _executor.execute(aTask);
  }

  /**
   * Submits a Callable task for execution and returns a Future representing that task.
   *
   * @param aTask The task to submit.
   * @return a Future representing pending completion of the task, and whose <tt>get()</tt> method
   *         will return <tt>null</tt> upon completion.
   * @throws IllegalStateException If this executor service is not running.
   * @throws NullPointerException If the task passed in is null.
   * @throws RejectedExecutionException If the task cannot be scheduled for execution.
   */
  public Future submit(Callable aTask) {
    if (!_isRunning) {
      throw new IllegalArgumentException("Cannot submit a task - the executor service is not running");
    }
    
    return _executor.submit(aTask);
  }
  
  public int getActiveThreadCount() {
    return _executor.getActiveCount();
  }
  
  public int getCurrentThreadPoolSize() {
    return _executor.getPoolSize();
  }

  public int getBufferedTaskCount() {
    return _executor.getQueue().size();
  }
  
  public long getCompletedTaskCount() {
    return _executor.getCompletedTaskCount();
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.ubik.monitor.FeedbackMonitorable#monitor()
   */
  public Properties monitor() throws Exception {
    Properties props = new Properties();
    props.setProperty("executor.name", _name);
    props.setProperty("executor.isRunning", String.valueOf(_isRunning));
    if (_isRunning) {
      props.setProperty("executor.threadPoolSize", String.valueOf(getCurrentThreadPoolSize()));
      props.setProperty("executor.activeThreadCount", String.valueOf(getActiveThreadCount()));
      props.setProperty("executor.maximumThreadPoolSize", String.valueOf(getMaximumPoolSize()));
      props.setProperty("executor.bufferedTaskCount", String.valueOf(getBufferedTaskCount()));
      props.setProperty("executor.taskQueueSize", String.valueOf(getTaskQueueSize()));
    }
    
    return props;
  }
  
  /**
   * This class implements a thread factory used by an executor.
   * 
   */
  public static class NamedThreadFactory implements ThreadFactory {

    /** The name of the pool associated with this thread factory. */
    private String _poolName;
    
    /** Thread group in which the created thread belongs. */
    private ThreadGroup _threadGroup;
    
    /** The prefix name to give to created threads. */
    private String _prefixName;

    /** The number of threads created by this factory. */
    private int _threadNumber;
    
    /**
     * Creates a new NamedThreadFactory instance.
     * 
     * @param aPoolName The name of the pool associated with this factory.
     */
    public NamedThreadFactory(String aPoolName) {
      _poolName = aPoolName;
      _prefixName = _poolName + "-Thread-";
      
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager == null) {
        _threadGroup = Thread.currentThread().getThreadGroup();
      } else {
        _threadGroup = securityManager.getThreadGroup();
      }
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    public Thread newThread(Runnable aTask) {
      Thread thread = new Thread(_threadGroup, aTask, _prefixName + (++_threadNumber));
      thread.setDaemon(false);
      thread.setPriority(Thread.NORM_PRIORITY);

      return thread;
    }
  }
}
