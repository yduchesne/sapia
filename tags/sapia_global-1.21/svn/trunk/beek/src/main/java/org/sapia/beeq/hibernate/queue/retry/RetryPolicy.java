package org.sapia.beeq.hibernate.queue.retry;

import java.util.Date;

/**
 * This interface specifies the behavior expected from "retry policies",
 * used in conjonction with {@link HibernateRetryQueue}s.
 * 
 * @author yduchesne
 *
 */
public interface RetryPolicy {

  /**
   * This method calculates the date of the next retry based on the
   * one of the last retry.
   * 
   * @param lastRetry the {@link Date} of the last retry, or <code>null</code> if
   * no retry has yet occurred.
   * @param currentRetryCount the current "retry count" (the current total amount of
   * time a retry was performed for a given message).
   * @return the next retry {@link Date}.
   */
  public Date calculateNextRetry(Date lastRetry, int currentRetryCount);
  
}
