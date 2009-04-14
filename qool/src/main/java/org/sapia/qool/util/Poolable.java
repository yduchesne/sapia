package org.sapia.qool.util;

import javax.jms.JMSException;

/**
 * This interface specifies the behavior that is expected from pooled objects.
 * @author yduchesne
 *
 */
public interface Poolable {
  
  /**
   * @return <code>true</code> if this instance is still valid and may be reinserted into
   * the pool.
   */
  public boolean recycle();

  /**
   * Invoked by the pool when this instance is to be dereferenced, in order to allow for cleanly 
   * releasing resources that it may hold.
   */
  public void dispose() throws JMSException;
}
