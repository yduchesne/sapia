package org.sapia.soto.util.concurrent;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class TestablePerishableObjectMonitorListener implements PerishableObjectMonitorListener {

  private List _expiredObjects;
  
  /**
   * Creates a new TestablePerishableObjectMonitorListener instance.
   */
  public TestablePerishableObjectMonitorListener() {
    _expiredObjects = new ArrayList();
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.util.concurrent.PerishableObjectMonitorListener#onExpiredObject(org.sapia.soto.util.concurrent.PerishableObject)
   */
  public void onExpiredObject(PerishableObject aPerishableObject) {
    synchronized (_expiredObjects) {
      _expiredObjects.add(aPerishableObject);
      _expiredObjects.notify();
    }
  }
  
  public void reset() {
    synchronized (_expiredObjects) {
      _expiredObjects.clear();
      _expiredObjects.notifyAll();
    }
  }

  
  public PerishableObject getNextExpiredObject() {
    synchronized (_expiredObjects) {
      if (_expiredObjects.isEmpty()) {
        return null;
      } else {
        return (PerishableObject) _expiredObjects.remove(0);
      }
    }
  }
  
  public PerishableObject awaitNextExpiredObject(long aTimeoutMillis) throws InterruptedException {
    synchronized (_expiredObjects) {
      if (_expiredObjects.isEmpty()) {
        _expiredObjects.wait(aTimeoutMillis);
        if (_expiredObjects.isEmpty()) {
          return null;
        } else {
          return (PerishableObject) _expiredObjects.remove(0);
        }
      } else {
        return (PerishableObject) _expiredObjects.remove(0);
      }
    }
  }

}
