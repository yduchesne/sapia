package org.sapia.soto.util.concurrent;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.LogFactory;
import org.sapia.soto.SotoContainer;

import junit.framework.TestCase;

/**
 * 
 *
 * @author Jean-Cedric Desrochers
 */
public class ExecutorServiceTest extends TestCase {

  public ExecutorServiceTest(String name){ super(name);}

  // Fixtures
  private ExecutorService _executor;
  private TestableCommand _command;
  
  public void setUp() throws Exception {
    _executor = new ExecutorService();
    _executor.setCorePoolSize(3);
    _executor.setMaximumPoolSize(5);
    _executor.setName(getName());
    _executor.setTaskQueueSize(5);
    _executor.init();
    _executor.start();
    
    _command = new TestableCommand();
  }
  
  public void tearDown() {
    _executor.dispose();
  }
  
  public void testExecute() throws Exception {
    _executor.execute(new CommandAdapter(_command));
    _command.waitForCompletion(500);
    assertTrue("The command is not completed", _command.isCompleted());
  }
  
  public void testSubmit() throws Exception {
    Future future = _executor.submit(new CommandAdapter(_command));
    future.get(500, TimeUnit.MILLISECONDS);
    assertTrue("The command is not completed", _command.isCompleted());
  }
  
  public void testSubmitWithError() throws Exception {
    RuntimeException kaboom = new RuntimeException("KABOOM!");
    _command.setCause(kaboom);
    Future future = _executor.submit(new CommandAdapter(_command));
    try {
      future.get(500, TimeUnit.MILLISECONDS);
      fail("The executed command should have thrown an exception");
    } catch (ExecutionException expected) {
      assertEquals("The cause of the execution exception is not valid", kaboom, expected.getCause());
    }
    assertTrue("The command is not completed", _command.isCompleted());
  }
  
  public void testSotoService() throws Exception {
    SotoContainer soto = new SotoContainer();
    try {
      soto.load(new File("etc/concurrent/executor.soto.xml"));
      soto.start();
      
      ExecutorService executor = (ExecutorService) soto.lookup("executor");
      assertNotNull("The executor service retrieved should not be null", executor);
    } finally {
      soto.dispose();
    }
  }
  
  
  /**
   * 
   */
  public class CommandAdapter implements Callable, Runnable {

    /** The command to execute. */
    private TestableCommand _command;
    
    /**
     * Creates a new CommandAdapter instance.
     * 
     * @param aCommand The command to adapt into a runnable.
     */
    public CommandAdapter(TestableCommand aCommand) {
      _command = aCommand;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
      try {
        call();
      } catch (Exception e) {
        LogFactory.getLog(CommandAdapter.class).error("Error running command", e);
      }
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    public Object call() throws Exception {
      _command.execute();
      return null;
    }
  }
}
