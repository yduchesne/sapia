package org.sapia.qool.util;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;

import junit.framework.TestCase;

public class AbstractPoolTest extends TestCase {

  public AbstractPoolTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }
  
  public void testAcquireRelease() throws Exception{
    TestPool p = new TestPool();
    
    List<TestPoolable> busy = new ArrayList<TestPoolable>();
    for(int i = 0; i < 10; i++){
      busy.add(p.acquire(new TestPoolConfig()));
    }
    
    assertEquals(busy.size(), p.getBusyCount());
    assertEquals(0, p.getAvailableCount());
    
    for(TestPoolable t:busy){
      p.release(t);
    }
    
    assertEquals(0, p.getBusyCount());
    assertEquals(busy.size(), p.getAvailableCount());
  }
  
  public void testClose() throws Exception{
    TestPool p = new TestPool();
    
    List<TestPoolable> busy = new ArrayList<TestPoolable>();
    for(int i = 0; i < 10; i++){
      busy.add(p.acquire(new TestPoolConfig()));
    }
    
    for(TestPoolable t:busy){
      p.release(t);
    }
    p.close();

    for(TestPoolable t:busy){
      assertTrue(t.disposed);
    }
  }
  
  public void testAcquireAfterClose() throws Exception{
    TestPool p = new TestPool();
    p.acquire(new TestPoolConfig());
    p.close();
    try{
      p.acquire(new TestPoolConfig());
      fail("Should not have been able to acquire object from closed pool");
    }catch(IllegalStateException e){
      //ok
    }
  }
  
  public void testPoolWithMaxSize() throws Exception{
    TestPool p = new TestPool();
    p.setMaxSize(5);
    p.setTimeout(100);
    for(int i = 0; i < 5; i++){
      p.acquire(new TestPoolConfig());
    }
    
    try{
      p.acquire(new TestPoolConfig());
      fail("Should not have been able to acquire");
    }catch(JMSException e){
      // ok 
    }
  }

}
