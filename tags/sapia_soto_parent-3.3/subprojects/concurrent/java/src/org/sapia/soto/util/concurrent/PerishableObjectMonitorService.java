package org.sapia.soto.util.concurrent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sapia.soto.Service;
//import org.sapia.soto.ubik.monitor.FeedbackMonitorable;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class PerishableObjectMonitorService implements PerishableObjectMonitor, Service /*, FeedbackMonitorable*/ {

  /** The logger instance of this service. */
  private Log _logger;
  
  /** Indicates if this monitor service is running or not. */
  private boolean _isRunning;

  /** The name of this object monitor service. */
  private String _name;
  
  /** Semaphore to synchronize operations. */
  private Semaphore _workingLock;
  
  /** The list of listeners on this monitor service. */
  private List _listeners;
  
  /** The list of registered objects with this monitor service. */
  private List _monitoredObjects;
  
  /** The map of registered perishable objects by their object id. */ 
  private Map _monitoredObjectsById;
  
  /** The moniroting task of this monitor executed by the monitoring thread. */
  private MonitoringTask _monitoringTask;
  
  /** The monitoring daemon thread of this monitor. */
  private Thread _monitoringDaemon;
  
  /** The thread that dispatches events to registered listeners. */
  private Thread _eventDispatcherThread;
  
  /** The list of expired objects that are discovered by this monitor service. */
  private List _expiredObjects;

  /** The total count of object registered with this monitor. */ 
  private int _totalRegisteredObjectCount;
  
  /** The total count of object expired with this monitor. */ 
  private int _totalExpiredObjectCount;
  
  /**
   * Creates a new PerishableObjectMonitorService instance.
   */
  public PerishableObjectMonitorService() {
    _logger = LogFactory.getLog(PerishableObjectMonitorService.class);
    _name = PerishableObjectMonitorService.class.getSimpleName();
    _workingLock = new Semaphore(1, true);
    _listeners = new ArrayList();
    _monitoredObjects = new ArrayList();
    _monitoredObjectsById = new HashMap();
    _expiredObjects = new ArrayList();
  }

  /**
   * Returns the logger instance of this monitor service.
   * 
   * @return The logger instance of this monitor service.
   */
  public Log getLogger() {
    return _logger;
  }
  
  /**
   * Changes the logger instance of this monitor service.
   * 
   * @param aLogger The new logger instance.
   */
  public void setLogger(Log aLogger) {
    _logger = aLogger;
  }
  
  /**
   * Returns the name value.
   *
   * @return The name value.
   */
  public String getName() {
    return _name;
  }

  /**
   * Changes the value of the name.
   *
   * @param aName The new name value.
   */
  public void setName(String aName) {
    _name = aName;
  }

  /**
   * Returns the value of the isRunning indicator of this monitor service.
   * 
   * @return True if this monitor service is running, false otherwise.
   */
  public boolean isRunning() {
    return _isRunning;
  }
  
  /* (non-Javadoc)
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
    _logger.debug("Initializing this perishable object monitor service");
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.Service#start()
   */
  public synchronized void start() throws Exception {
    if (!_isRunning) {
      _logger.info("Starting this perishable object monitor service");
      _isRunning = true;
      
      _eventDispatcherThread = new Thread(new EventDispatchTask(this), _name+"-EventDispatcher");
      _eventDispatcherThread.start();
      
      _monitoringTask = new MonitoringTask(this);
      _monitoringDaemon = new Thread(_monitoringTask, _name+"-MonitoringDaemon");
      _monitoringDaemon.setDaemon(true);
      _monitoringDaemon.start();
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
    if (_isRunning) {
      _logger.info("Stopping this perishable object monitor service");
      _isRunning = false;
      
      _monitoringDaemon.interrupt();
      _monitoringDaemon = null;
      
      _eventDispatcherThread.interrupt();
      _eventDispatcherThread = null;
      
      _listeners.clear();
      _monitoredObjects.clear();
      _monitoredObjectsById.clear();
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.util.concurrent.PerishableObjectMonitor#addListener(org.sapia.soto.util.concurrent.PerishableObjectMonitorListener)
   */
  public void addListener(PerishableObjectMonitorListener aListener) {
    ArrayList newList = new ArrayList(_listeners);
    newList.add(aListener);

    // To prevent concurrent errors with iterators
    _listeners = newList;
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.util.concurrent.PerishableObjectMonitor#removeListener(org.sapia.soto.util.concurrent.PerishableObjectMonitorListener)
   */
  public boolean removeListener(PerishableObjectMonitorListener aListener) {
    if (_listeners.contains(aListener)) {
      ArrayList newList = new ArrayList(_listeners);
      newList.remove(aListener);

      // To prevent concurrent errors with iterators
      _listeners = newList;
      return true;
      
    } else {
      return false;
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.util.concurrent.PerishableObjectMonitor#register(org.sapia.soto.util.concurrent.PerishableObject)
   */
  public void register(PerishableObject aPerishableObject) {
    if (!_isRunning) {
      throw new IllegalStateException("The perishable object monitor service is not running");
    } else if (aPerishableObject == null) {
      throw new IllegalArgumentException("The perishable object passed in is null");
    }

    boolean isLockAcquired = false;
    try {
      _workingLock.acquire();
      isLockAcquired = true;
      
      Long objectId = new Long(aPerishableObject.getObjectId());
      if (_monitoredObjectsById.containsKey(objectId)) {
        throw new IllegalArgumentException("Could not register the perishable object passed in: another object is already registered for the id " + objectId);
      }
      
      MonitoredObject monitored = new MonitoredObject(aPerishableObject);
      _monitoredObjects.add(monitored);
      Collections.sort(_monitoredObjects, MonitoredObject.OBJECT_COMPARATOR);
      _monitoredObjectsById.put(objectId, monitored);

      if (_logger.isDebugEnabled()) {
        _logger.debug("Registered a perishable object with the monitor service [objectId=" +
                objectId + " monitoredObject=" + monitored + "]");
      }

      _totalRegisteredObjectCount++;
      updateMonitoringTaskTimeout();

    } catch (InterruptedException ie) {
      throw new IllegalStateException("The current thread was interrupted while waiting on acquiring the working lock", ie);
      
    } finally {
      if (isLockAcquired) {
        _workingLock.release();
      }
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.util.concurrent.PerishableObjectMonitor#renew(org.sapia.soto.util.concurrent.PerishableObject)
   */
  public void renew(PerishableObject aPerishableObject) {
    if (!_isRunning) {
      throw new IllegalStateException("The perishable object monitor service is not running");
    } else if (aPerishableObject == null) {
      throw new IllegalArgumentException("The perishable object passed in is null");
    }

    renew(new PerishableObject[] {aPerishableObject});
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.util.concurrent.PerishableObjectMonitor#renew(org.sapia.soto.util.concurrent.PerishableObject[])
   */
  public void renew(PerishableObject[] somePerishableObjects) {
    if (!_isRunning) {
      throw new IllegalStateException("The perishable object monitor service is not running");
    } else if (somePerishableObjects == null) {
      throw new IllegalArgumentException("The perishable object array passed in is null");
    }

    boolean isLockAcquired = false;
    try {
      _workingLock.acquire();
      isLockAcquired = true;

      // 1. reset all the specified objects
      for (PerishableObject pObject: somePerishableObjects) {
        MonitoredObject monitored = (MonitoredObject) _monitoredObjectsById.get(pObject.getObjectId());
        if (monitored != null) {
          monitored.reset();
          
          if (_logger.isDebugEnabled()) {
            _logger.debug("Renwed a perishable object with the monitor service [objectId=" +
                    pObject.getObjectId() + " monitoredObject=" + monitored + "]");
          }

        } else {
          _logger.warn("Unable to renew the perishable object from the monitor service - object id "
                  + pObject.getObjectId() + " not found");
        }
      }

      // 2. resort the list of monitored objects
      Collections.sort(_monitoredObjects, MonitoredObject.OBJECT_COMPARATOR);
      
      // 3. update task
      updateMonitoringTaskTimeout();

    } catch (InterruptedException ie) {
      throw new IllegalStateException("The current thread was interrupted while waiting on acquiring the working lock", ie);
      
    } finally {
      if (isLockAcquired) {
        _workingLock.release();
      }
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.util.concurrent.PerishableObjectMonitor#refreshPerishableObjects()
   */
  public void refreshPerishableObjects() {
    boolean isLockAcquired = false;
    try {
      _workingLock.acquire();
      isLockAcquired = true;
      
      if (_monitoredObjects.size() == 1) {
        ((MonitoredObject) _monitoredObjects.get(0)).calculateExpirationInstant();
      } else {
        Collections.sort(_monitoredObjects, MonitoredObject.OBJECT_COMPARATOR);
      }
      updateMonitoringTaskTimeout();

    } catch (InterruptedException ie) {
      throw new IllegalStateException("The current thread was interrupted while waiting on acquiring the working lock", ie);
      
    } finally {
      if (isLockAcquired) {
        _workingLock.release();
      }
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.util.concurrent.PerishableObjectMonitor#unregister(org.sapia.soto.util.concurrent.PerishableObject)
   */
  public boolean unregister(PerishableObject aPerishableObject) {
    if (!_isRunning) {
      throw new IllegalStateException("The perishable object monitor service is not running");
    } else if (aPerishableObject == null) {
      throw new IllegalArgumentException("The perishable object passed in is null");
    }
    
    boolean result = false;
    boolean isLockAcquired = false;
    try {
      _workingLock.acquire();
      isLockAcquired = true;
      
      Long objectId = new Long(aPerishableObject.getObjectId());
      MonitoredObject monitored = (MonitoredObject) _monitoredObjectsById.remove(objectId);
      if (monitored != null) {
        _monitoredObjects.remove(monitored);
        result = true;

        if (_logger.isDebugEnabled()) {
          _logger.debug("Unregistered a perishable object with the monitor service [objectId=" +
                  objectId + " monitoredObject=" + monitored + "]");
        }

      } else {
        _logger.warn("Unable to unregister the perishable object from the monitor service - object id "
                + objectId + " not found");
      }

      updateMonitoringTaskTimeout();

    } catch (InterruptedException ie) {
      throw new IllegalStateException("The current thread was interrupted while waiting on acquiring the working lock", ie);
      
    } finally {
      if (isLockAcquired) {
        _workingLock.release();
      }
    }
    
    return result;
  }
  
  /**
   * Internal method to modified the monitoring task running in the daemon thread
   * according to the current state of the monitor.
   */
  protected void updateMonitoringTaskTimeout() {
    if (_monitoredObjects.size() > 0) {
      MonitoredObject monitored = (MonitoredObject) _monitoredObjects.get(0);
      if (monitored == null) {
        _monitoredObjects.remove(0);
        updateMonitoringTaskTimeout();
      } else {
        _monitoringTask.setNextWakeupInstant(monitored.getExpirationInstant());
      }
    } else {
      _monitoringTask.setNextWakeupInstant(0);
    }
  }

  /**
   * Method called by the monitoring task when at least one perishable object
   * is expired (time to live reached).
   */
  protected void onExpiredObjectDetected() {
    _logger.info("On expired perishable object detected...");

    boolean isLockAcquired = false;
    try {
      _workingLock.acquire();
      isLockAcquired = true;
      
      boolean isDone = false;
      while (!isDone && _monitoredObjects.size() > 0) {
        MonitoredObject monitored = (MonitoredObject) _monitoredObjects.get(0);
        if (monitored == null) {
          _logger.warn("A monitored object is expired but a null monitored object was taken out of the list!!!");
          _monitoredObjects.remove(0);
  
        } else if (System.currentTimeMillis() >= monitored.getExpirationInstant()) {
          Long objectId = new Long(monitored.getObject().getObjectId());
          _monitoredObjects.remove(monitored);
          _monitoredObjectsById.remove(objectId);
  
          synchronized (_expiredObjects) {
            _expiredObjects.add(monitored.getObject());
          }
  
          _totalExpiredObjectCount++;
          _logger.warn("The perishable object expired [objectId=" + objectId + "object=" + monitored);
          
        } else {
          isDone = true;
        }
      }

      synchronized (_expiredObjects) {
        _expiredObjects.notify();
      }
      
      updateMonitoringTaskTimeout();

    } catch (InterruptedException ie) {
      throw new IllegalStateException("The current thread was interrupted while waiting on acquiring the working lock", ie);
      
    } finally {
      if (isLockAcquired) {
        _workingLock.release();
      }
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.ubik.monitor.FeedbackMonitorable#monitor()
   */
  public Properties monitor() throws Exception {
    Properties props = new Properties();
    props.setProperty("objectMonitor.isRunning", String.valueOf(_isRunning));
    props.setProperty("objectMonitor.listenerCount", String.valueOf(_listeners.size()));
    props.setProperty("objectMonitor.monitoredObjectCount", String.valueOf(_monitoredObjects.size()));
    props.setProperty("objectMonitor.totalRegisteredObjectCount", String.valueOf(_totalRegisteredObjectCount));
    props.setProperty("objectMonitor.totalExpiredObjectCount", String.valueOf(_totalExpiredObjectCount));
    
    return props;
  }

  public int getMonitoredObjectCount() {
    return _monitoredObjects.size();
  }
  
  public long getTotalRegisteredObjectCount() {
    return _totalRegisteredObjectCount;
  }
  
  public long getTotalExpiredObjectCount() {
    return _totalExpiredObjectCount;
  }
  
  
 /**
  * Internal monitoring task executed by the daemon thread of the monitor
  * to flag the perishable objects that reached their time to live.
  *
  * @author Jean-Cédric Desrochers
  */
  public static class MonitoringTask implements Runnable {

    /** The monitor of this task. */
    private PerishableObjectMonitorService _monitor;
    
    /** The next wake up instant of this task. */
    private long _nextWakeUpInstant;
    
    /** Lock object for synchronization. */
    private Object _lock;
    
    /**
     * Creates a new MonitoringTask.
     * 
     * @param aMonitor The monitor that uses this task.
     */
    public MonitoringTask(PerishableObjectMonitorService aMonitor) {
      _monitor = aMonitor;
      _lock = new Object();
    }
    
    /**
     * Returns the next wake up instant of this monitoring task.
     * 
     * @return The next wake up instant of this monitoring task.
     */
    public long getNextWakeUptInstant() {
      return _nextWakeUpInstant;
    }
    
    /**
     * Changes the next wake up instant of this monitoring task. This
     * method will affect the behaviour of the monitoring thread: if it
     * sleeps it will be waken up to use the new timeout instant.
     * 
     * @param anInstant The new wake up instant.
     */
    public void setNextWakeupInstant(long anInstant) {
      if (anInstant != _nextWakeUpInstant) {
        synchronized (_lock) {
          _nextWakeUpInstant = anInstant;
          _lock.notify();
        }
      }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
      long lastException = 0;
      while (_monitor.isRunning()) {
        try {
          // Sync block for wait() or sleep()
          long wakeUpValue;
          synchronized (_lock) {
            wakeUpValue = _nextWakeUpInstant;
            if (wakeUpValue == 0) {
              _lock.wait();
            } else {
              long now = System.currentTimeMillis();
              if (wakeUpValue > now) {
                _lock.wait(wakeUpValue - now);
              }
            }
          }

          if (wakeUpValue == _nextWakeUpInstant) {
            // Calls back the parent for invalidity processing
            _monitor.onExpiredObjectDetected();
          }

        } catch (InterruptedException ie) {
          // noop - used by shutdown mechanism if waiting

        } catch (RuntimeException re) {
          _monitor._logger.error("System error in the monitoring task of the perishable object monitor", re);
          
          // logic to avoid inifinte loop that takes 100% of cpu
          if ((System.currentTimeMillis() - lastException) <= 1000) {
            try {
              Thread.sleep(1000);
            } catch (InterruptedException ie) {
            }
          }
          lastException = System.currentTimeMillis();
        }
      }
    }
  }
  

  
  /**
   * Internal task executed by the event dispatcher thread that notified
   * the registered listerners of this monitor service.
   *
   * @author Jean-Cédric Desrochers
   */
  public static class EventDispatchTask implements Runnable {

    /** The monitor of this task. */
    private PerishableObjectMonitorService _monitor;

    /**
     * Creates a new EventDispatchTask instance.
     * 
     * @param aMonitor The monitor that uses this event dispatch task.
     */
    public EventDispatchTask(PerishableObjectMonitorService aMonitor) {
      _monitor = aMonitor;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
      while (_monitor.isRunning()) {
        try {
          synchronized (_monitor._expiredObjects) {
            if (_monitor._expiredObjects.isEmpty()) {
              _monitor._expiredObjects.wait();
            }
          }

          while (!_monitor._expiredObjects.isEmpty()) {
            PerishableObject object;
            synchronized (_monitor._expiredObjects) {
              object = (PerishableObject) _monitor._expiredObjects.remove(0);
            }

            fireExpiredEvent(object);
          }

        } catch (InterruptedException ie) {
          // noop - used by shutdown mechanism if waiting
          
        } catch (RuntimeException re) {
          _monitor._logger.error("System error while dispatching perishable object event", re);
        }
      }
    }

    /**
     * Internal object that fires en expired event to the registered listeners.
     * 
     * @param anObject The perishable object that is now expired.
     */
    protected void fireExpiredEvent(PerishableObject anObject) {
      for (Iterator it = _monitor._listeners.iterator(); it.hasNext(); ) {
        try {
          PerishableObjectMonitorListener listener = (PerishableObjectMonitorListener) it.next();
          listener.onExpiredObject(anObject);
        } catch (RuntimeException re) {
          _monitor._logger.error("System error calling back perishable object monitor listener on an expired event", re);
        }
      }
    }
  }
  
}

