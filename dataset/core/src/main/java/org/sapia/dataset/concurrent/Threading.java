package org.sapia.dataset.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.sapia.dataset.util.Time;

/**
 * Provides the default {@link ExecutorService} used for concurrent computations.
 * 
 * @author yduchesne
 */
public class Threading {

  
  private static final Time DEFAULT_ASYNC_TIMEOUT = new Time(30, TimeUnit.SECONDS);
  private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
  
  static {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        EXECUTOR.shutdownNow();
      }
    });
  }
  
  private Threading() {
  }
      
  /**
   * @return the default {@link ExecutorService}.
   */
  public static ExecutorService getThreadPool() {
    return EXECUTOR;
  }
  
  /**
   * @return the default timeout for async computations.
   */
  public static Time getTimeout() {
    return DEFAULT_ASYNC_TIMEOUT;
  }
}
