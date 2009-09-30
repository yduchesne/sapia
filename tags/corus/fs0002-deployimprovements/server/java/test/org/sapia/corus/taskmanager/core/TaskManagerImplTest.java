package org.sapia.corus.taskmanager.core;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.apache.log.Hierarchy;
import org.sapia.corus.TestServerContext;

public class TaskManagerImplTest extends TestCase {

  private TaskManagerImpl tm;
  
  protected void setUp() throws Exception {
    super.setUp();
    tm = new TaskManagerImpl(
        Hierarchy.getDefaultHierarchy().getLoggerFor("taskmanager"), 
        TestServerContext.create());
  }

  protected void tearDown() throws Exception {
    super.tearDown();
    tm.shutdown();
  }

  public void testExecute() throws Exception{
    TestTask t = new TestTask();
    tm.execute(t);
    t.waitFor();
    assertTrue("Not completed", t.completed);
  }

  public void testExecuteAndWait() throws Exception{
    TestTask t = new TestTask();
    String result = (String)tm.executeAndWait(t).get();
    assertEquals("Result invalid", "TEST", result);
  }
  
  public void testExecuteAndWaitError() throws Exception{
    ErrorTask t = new ErrorTask();
    try{
      tm.executeAndWait(t).get();
      fail("Expected InvocationTargetException");
    }catch(InvocationTargetException e){
      //ok
    }
  }

  public void testExecuteAndWaitTaskWithLog() throws Exception{
    TestTask t = new TestTask();
    TestTaskLog log = new TestTaskLog();
    tm.executeAndWait(t, log).get();
    assertTrue("Parent log was not called", log.logged);
  }

  public void testExecuteBackground() throws Exception{
    TestTask t = new TestTask();
    tm.executeBackground(200, 200, t);
    Thread.sleep(1000);
    assertTrue("Task executed only once or less", t.getExecutionCount() > 1);
  }
  
  public void testExecuteBackgroundWithMax() throws Exception{
    TestTask t = new TestTask();
    t.setMaxExecution(3);
    tm.executeBackground(200, 200, t);
    Thread.sleep(1000);
    assertTrue("Task executed more than max", t.getExecutionCount() > 0 && t.getExecutionCount() <= 3);
  }
  
  public void testForkTask() throws Exception{
    TestTask t = new TestTask();
    tm.fork(t);
    t.waitFor();
    assertTrue("Task was not executed", t.completed);
  }

  public void testForkTaskWithListener() throws Exception{
    TestTask t = new TestTask();
    TestTaskListener ls = new TestTaskListener();
    tm.fork(t, ls);
    t.waitFor();
    assertTrue("Task listener not called", ls.succeeded);
  }
  
  public void testForkTaskErrorWithListener() throws Exception{
    ErrorTask t = new ErrorTask();
    TestTaskListener ls = new TestTaskListener();
    tm.fork(t, ls);
    t.waitFor();
    assertTrue("Task listener not called", ls.failed);
  }

  
  class TestTask extends Task{
    boolean completed;
    @Override
    public synchronized Object execute(TaskExecutionContext ctx) throws Throwable {
      try{
        ctx.getLog().debug(this, "executing... " + getExecutionCount());
        return "TEST";
      }finally{
        completed = true;
        notifyAll();
      }
    }
    
    public synchronized void waitFor() throws InterruptedException{
      while(!completed){
        wait(); 
      }
    }
  }

  class ErrorTask extends TestTask{
    @Override
    public Object execute(TaskExecutionContext ctx) throws Throwable {
      try{
        throw new Exception();
      }finally{
        completed = true;
        notifyAll();
      }
    }
  }
  
  class TestTaskLog implements TaskLog{
    boolean logged;
    
    public void debug(Task task, String msg) {
      this.logged = true;
    }
    public void info(Task task, String msg) {
      this.logged = true;
    }
    public void warn(Task task, String msg) {
      this.logged = true;
    }
    public void warn(Task task, String msg, Throwable err) {
      this.logged = true;
    }
    public void error(Task task, String msg) {
      this.logged = true;
    }
    public void error(Task task, String msg, Throwable err) {
      this.logged = true;
    }
    
    public void close() {
    }
  }
  
  class TestTaskListener implements TaskListener{

    boolean failed, succeeded;
    public void executionFailed(Task task, Throwable err) {
      failed = true;
    }
    
    public void executionSucceeded(Task task, Object result) {
      succeeded = true;
    }
    
  }
}
