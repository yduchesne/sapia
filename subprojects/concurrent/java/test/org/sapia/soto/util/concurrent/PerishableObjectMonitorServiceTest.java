package org.sapia.soto.util.concurrent;

import java.io.File;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.sapia.soto.SotoContainer;
import org.sapia.soto.util.Utils;

import junit.framework.TestCase;

/**
 *
 * @author Jean-Cedric Desrochers
 */
public class PerishableObjectMonitorServiceTest extends TestCase {

  private SotoContainer _soto;
  private PerishableObjectMonitorService _monitor;
  private TestablePerishableObjectMonitorListener _listener;

  public PerishableObjectMonitorServiceTest(String name){ super(name);}
  
  public void setUp() throws Exception {
    _soto = new SotoContainer();
    _soto.load(new File("etc/concurrent/perishableObjectMonitor.soto.xml"));
    _soto.start();
    
    _monitor = (PerishableObjectMonitorService) _soto.lookup("perishableObjectMonitor");
    _listener = new TestablePerishableObjectMonitorListener();
    _monitor.addListener(_listener);
  }
  
  public void tearDown() {
    _monitor.removeListener(_listener);
   _soto.dispose();
  }
  
  void doAssertFalse(String msg, boolean result){
    assertTrue(msg, !result);
  }
    
  
  public void testOneObject_noExpiration() {
    TestObject object = new TestObject(5000);
    _monitor.register(object);
    Utils.sleepUninterruptedly(500);
    
    assertTrue("Should unregister object with success, but returned false", _monitor.unregister(object));
    assertMonitor(0, 1, 0, _monitor);
  }
  
  public void testOneObject_withExpiration() {
    TestObject object = new TestObject(250);
    _monitor.register(object);
    Utils.sleepUninterruptedly(500);
    
    assertEquals("The expired object is invalid", object, _listener.getNextExpiredObject());
    doAssertFalse("Should not unregister object with success, but returned true", _monitor.unregister(object));
    assertMonitor(0, 1, 1, _monitor);
  }
  
  public void testOneObject_withRenewal() throws Exception {
    TestObject object = new TestObject(500);
    _monitor.register(object);
    long startTime = System.currentTimeMillis();
    Utils.sleepUninterruptedly(250);
    _monitor.renew(object);

    PerishableObject expired = _listener.awaitNextExpiredObject(2000);
    long elapse = System.currentTimeMillis() - startTime;
System.err.println("===> waiting time millis: " + elapse);
    assertEquals("The expired object is invalid", object, expired);
    assertTrue("The waiting time is invalid - expected >= 750 but was: " + elapse, elapse >= 750);
    doAssertFalse("Should not unregister object with success, but returned true", _monitor.unregister(object));
    assertMonitor(0, 1, 1, _monitor);
  }
  
  public void testTwoObjects_noExpiration() {
    TestObject object1 = new TestObject(5000);
    TestObject object2 = new TestObject(5000);
    
    _monitor.register(object1);
    Utils.sleepUninterruptedly(250);
    _monitor.register(object2);
    Utils.sleepUninterruptedly(250);
    
    assertMonitor(2, 2, 0, _monitor);
    assertTrue("Should unregister object 1 with success, but returned false", _monitor.unregister(object1));
    assertMonitor(1, 2, 0, _monitor);
    assertTrue("Should unregister object 2 with success, but returned false", _monitor.unregister(object2));
    assertMonitor(0, 2, 0, _monitor);
  }
  
  public void testTwoObjects_singleExpiration() {
    TestObject object1 = new TestObject(5000);
    TestObject object2 = new TestObject(100);
    
    _monitor.register(object1);
    Utils.sleepUninterruptedly(250);
    _monitor.register(object2);
    Utils.sleepUninterruptedly(250);
    
    assertEquals("The expired object is invalid", object2, _listener.getNextExpiredObject());
    assertMonitor(1, 2, 1, _monitor);
    assertTrue("Should unregister object 1 with success, but returned false", _monitor.unregister(object1));
    assertMonitor(0, 2, 1, _monitor);
    doAssertFalse("Should not unregister object 2 with success, but returned true", _monitor.unregister(object2));
    assertMonitor(0, 2, 1, _monitor);
  }
  
  public void testTwoObjects_sequentialExpiration() {
    TestObject object1 = new TestObject(300);
    _monitor.register(object1);
    Utils.sleepUninterruptedly(200);
    assertMonitor(1, 1, 0, _monitor);
    
    TestObject object2 = new TestObject(250);
    _monitor.register(object2);
    Utils.sleepUninterruptedly(150);
    assertMonitor(1, 2, 1, _monitor);
    
    Utils.sleepUninterruptedly(200);
    assertEquals("The expired object 1 is invalid", object1, _listener.getNextExpiredObject());
    doAssertFalse("Should not unregister object 1 with success, but returned true", _monitor.unregister(object1));
    assertEquals("The expired object 2 is invalid", object2, _listener.getNextExpiredObject());
    doAssertFalse("Should not unregister object 2 with success, but returned true", _monitor.unregister(object2));
    assertMonitor(0, 2, 2, _monitor);
  }
  
  public void testTwoObjects_inversedExpiration() {
    TestObject object1 = new TestObject(400);
    _monitor.register(object1);
    Utils.sleepUninterruptedly(200);
    assertMonitor(1, 1, 0, _monitor);
    
    TestObject object2 = new TestObject(50);
    _monitor.register(object2);
    Utils.sleepUninterruptedly(150);
    assertMonitor(1, 2, 1, _monitor);
    
    Utils.sleepUninterruptedly(200);
    assertEquals("The expired object 2 is invalid", object2, _listener.getNextExpiredObject());
    doAssertFalse("Should not unregister object 2 with success, but returned true", _monitor.unregister(object2));
    
    assertEquals("The expired object 1 is invalid", object1, _listener.getNextExpiredObject());
    doAssertFalse("Should not unregister object 1 with success, but returned true", _monitor.unregister(object1));
    assertMonitor(0, 2, 2, _monitor);
  }
  
  public void testMultiThreaded() throws Exception {
    int maxThread = 250;
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch completionLatch = new CountDownLatch(1+maxThread);

    for (int i = 1; i <= maxThread; i++) {
      new Thread(new NormalObjectTask(20, startLatch, completionLatch), "STRESS-NORMAL#"+maxThread).start();
    }
    new Thread(new TimeoutRequestTask(100, startLatch, completionLatch), "STRESS-TIMEOUT").start();
    
    startLatch.countDown();

    assertTrue("Could not complete the multi threaded test case: timeout", completionLatch.await(60, TimeUnit.SECONDS));
    assertEquals("The monitored object count is invalid", 0, _monitor.getMonitoredObjectCount());
  }
  
  
  
  public static void assertMonitor(int eMonitoredObjectCount, long eTotalRegisteredObjectCount,
          long eTotalExpiredObjectCount, PerishableObjectMonitorService actual) {
    assertNotNull("The perishable object monitor service passed in should not be null", actual);
    assertEquals("The monitored object count of the monitor service is invalid", eMonitoredObjectCount, actual.getMonitoredObjectCount());
    assertEquals("The total registered object count of the monitor service is invalid", eTotalRegisteredObjectCount, actual.getTotalRegisteredObjectCount());
    assertEquals("The total expired object count of the monitor service is invalid", eTotalExpiredObjectCount, actual.getTotalExpiredObjectCount());
  }
  
  
  public static class TestObject implements PerishableObject {

    private static long _SEQUENCE;
    
    private long _objectId;
    private long _timeToLiveMillis;
    
    public TestObject(long aTimeToLiveMillis) {
      _objectId = ++_SEQUENCE;
      _timeToLiveMillis = aTimeToLiveMillis;
    }
    
    /* (non-Javadoc)
     * @see org.sapia.soto.util.concurrent.PerishableObject#getObjectId()
     */
    public long getObjectId() {
      return _objectId;
    }

    /* (non-Javadoc)
     * @see org.sapia.soto.util.concurrent.PerishableObject#getTimeToLiveMillis()
     */
    public long getTimeToLiveMillis() {
      return _timeToLiveMillis;
    }
  }
  
  
  
  /**
   * 
   */
  public class NormalObjectTask implements Runnable {
    private int _maxLoopCount;
    private int _iterationCount;
    private CountDownLatch _startLatch;
    private CountDownLatch _completionLatch;
    
    public NormalObjectTask(int aLoopCount, CountDownLatch aStartLatch, CountDownLatch aCompletionLatch) {
      _maxLoopCount = aLoopCount;
      _iterationCount = 0;
      _startLatch = aStartLatch;
      _completionLatch = aCompletionLatch;
    }
    
    public void run() {
      try {
        _startLatch.await();
        while (_iterationCount++ < _maxLoopCount) {
          try {
            TestObject object = new TestObject(2000);
            _monitor.register(object);
            Thread.sleep(450);
            _monitor.unregister(object);
            Thread.sleep(50);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }

      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        _completionLatch.countDown();
      }
    }
  }
  
  /**
   * 
   */
  public class TimeoutRequestTask implements Runnable {
    private int _maxLoopCount;
    private int _iterationCount;
    private CountDownLatch _startLatch;
    private CountDownLatch _completionLatch;
    
    public TimeoutRequestTask(int aLoopCount, CountDownLatch aStartLatch, CountDownLatch aCompletionLatch) {
      _maxLoopCount = aLoopCount;
      _iterationCount = 0;
      _startLatch = aStartLatch;
      _completionLatch = aCompletionLatch;
    }
    
    public void run() {
      try {
        _startLatch.await();
        while (_iterationCount++ < _maxLoopCount && _completionLatch.getCount() > 1) {
          try {
            TestObject object = new TestObject(750); 
            _monitor.register(object);
            TestObject expired = (TestObject) _listener.awaitNextExpiredObject(10000);
            assertEquals("The expired test object is invalid", object, expired);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }

      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        _completionLatch.countDown();
      }
    }
  }
}
