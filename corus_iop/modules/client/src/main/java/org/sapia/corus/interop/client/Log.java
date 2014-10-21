package org.sapia.corus.interop.client;


/**
 * Defines logging behavior.
 *
 * @author Yanick Duchesne
 */
public interface Log {
  public void debug(Object o);

  public void info(Object o);

  public void info(Object o, Throwable t);
  
  public void warn(Object o);

  public void warn(Object o, Throwable t);

  public void fatal(Object o);

  public void fatal(Object o, Throwable t);
  
  public boolean isDebugEnabled();
  
  public boolean isInfoEnabled();
  
  public boolean isWarnEnabled();
  
}
