package org.sapia.soto.activemq;

import java.io.IOException;

import org.apache.activemq.store.jdbc.DatabaseLocker;
import org.apache.activemq.store.jdbc.JDBCPersistenceAdapter;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class JDBCPersistenceAdapterEx extends JDBCPersistenceAdapter {

  private int _lockKeepAlivePeriodMillis = 0;
  
  public int getLockKeepAlivePeriod() {
    return _lockKeepAlivePeriodMillis;
  }
  
  public void setLockKeepAlivePeriod(int aPeriodMillis) {
    _lockKeepAlivePeriodMillis = aPeriodMillis;
  }

  /* (non-Javadoc)
   * Override the parent's method to work around the problem of the lock keep alive period
   * parameter that is not configurable (https://issues.apache.org/activemq/browse/AMQ-1310).
   * 
   * @see org.apache.activemq.store.jdbc.JDBCPersistenceAdapter#createDatabaseLocker()
   */
  protected DatabaseLocker createDatabaseLocker() throws IOException {
    DatabaseLocker locker = super.createDatabaseLocker();
    if (_lockKeepAlivePeriodMillis > 0) {
      getScheduledThreadPoolExecutor().scheduleAtFixedRate(new Runnable() {
          public void run() {
              databaseLockKeepAlive();
          }
      }, _lockKeepAlivePeriodMillis, _lockKeepAlivePeriodMillis, TimeUnit.MILLISECONDS);
  }
    return locker;
  }
  
}
