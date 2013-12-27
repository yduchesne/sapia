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
public class ThrottledExecutorServiceTest extends TestCase {

  public ThrottledExecutorServiceTest (String name){ super(name); }

  // Fixtures
  private ThrottledExecutorService _executor;
  private TestableCommand _command1, _command2, _command3;
  
  public void setUp() throws Exception {
    _executor = new ThrottledExecutorService();
    _executor.setName(getName());
    _executor.setCorePoolSize(3);
    _executor.setMaxThroughputPerSecond(4);
    _executor.setThreadKeepAliveTime(30000);
    _executor.init();
    _executor.start();
    
    _command1 = new TestableCommand();
    _command2 = new TestableCommand();
    _command3 = new TestableCommand();
  }
  
  public void tearDown() {
    _executor.dispose();
  }
  
  public void testExecute_ImplicitThrottling() throws Exception {
    _executor.setThrottlingExplicit(false);
    _executor.execute(new CommandAdapter(_command1));
    _executor.execute(new CommandAdapter(_command2));
    _executor.execute(new CommandAdapter(_command3));
    
    _command1.waitForCompletion(500);
    _command2.waitForCompletion(500);
    _command3.waitForCompletion(500);
    
    assertTrue("The command 1 is not completed", _command1.isCompleted());
    assertTrue("The command 2 is not completed", _command2.isCompleted());
    assertTrue("The command 3 is not completed", _command3.isCompleted());

    assertTrue("The delay before execution of command 1 should be less than 250 millis: but was " + _command1.getDelayBeforeExecution(),
            _command1.getDelayBeforeExecution() < 250);
    assertTrue("The delay before execution of command 2 should be greater than 250 millis: but was " + _command2.getDelayBeforeExecution(),
            _command2.getDelayBeforeExecution() >= 250);
    assertTrue("The delay before execution of command 3 should be greater than 250 millis: but was " + _command3.getDelayBeforeExecution(),
            _command3.getDelayBeforeExecution() >= 250);
  }
  
  public void testExecute_ExplicitThrottling() throws Exception {
    _executor.setThrottlingExplicit(true);
    _executor.execute(new CommandAdapter(_command1));
    _executor.execute(new CommandAdapter(_command2));
    _executor.execute(new CommandAdapter(_command3));
    
    _command1.waitForCompletion(500);
    _command2.waitForCompletion(500);
    _command3.waitForCompletion(500);
    
    assertTrue("The command 1 is not completed", _command1.isCompleted());
    assertTrue("The command 2 is not completed", _command2.isCompleted());
    assertTrue("The command 3 is not completed", _command3.isCompleted());

    assertTrue("The delay before execution of command 1 should be less than 250 millis: but was " + _command1.getDelayBeforeExecution(),
            _command1.getDelayBeforeExecution() < 250);
    assertTrue("The delay before execution of command 2 should be greater than 250 millis: but was " + _command2.getDelayBeforeExecution(),
            _command2.getDelayBeforeExecution() < 250);
    assertTrue("The delay before execution of command 3 should be greater than 250 millis: but was " + _command3.getDelayBeforeExecution(),
            _command3.getDelayBeforeExecution() < 250);
  }
  
  public void testExecuteThrottled() throws Exception {
    _executor.executeThrottled(new CommandAdapter(_command1));
    _executor.executeThrottled(new CommandAdapter(_command2));
    _executor.executeThrottled(new CommandAdapter(_command3));

    _command1.waitForCompletion(500);
    _command2.waitForCompletion(500);
    _command3.waitForCompletion(500);
    
    assertTrue("The command 1 is not completed", _command1.isCompleted());
    assertTrue("The command 2 is not completed", _command2.isCompleted());
    assertTrue("The command 3 is not completed", _command3.isCompleted());
    
    assertTrue("The delay before execution of command 1 should be less than 250 millis: but was " + _command1.getDelayBeforeExecution(),
            _command1.getDelayBeforeExecution() < 250);
    assertTrue("The delay before execution of command 2 should be greater than 250 millis: but was " + _command2.getDelayBeforeExecution(),
            _command2.getDelayBeforeExecution() >= 250);
    assertTrue("The delay before execution of command 3 should be greater than 250 millis: but was " + _command3.getDelayBeforeExecution(),
            _command3.getDelayBeforeExecution() >= 250);
  }
  
  public void testSubmit_ImplicitThrottling() throws Exception {
    _executor.setThrottlingExplicit(false);
    Future future1 = _executor.submit(new CommandAdapter(_command1));
    Future future2 = _executor.submit(new CommandAdapter(_command2));
    Future future3 = _executor.submit(new CommandAdapter(_command3));
    
    future1.get(500, TimeUnit.MILLISECONDS);
    future2.get(500, TimeUnit.MILLISECONDS);
    future3.get(500, TimeUnit.MILLISECONDS);

    assertTrue("The command is not completed", _command1.isCompleted());
    assertTrue("The command is not completed", _command2.isCompleted());
    assertTrue("The command is not completed", _command3.isCompleted());
    
    assertTrue("The delay before execution of command 1 should be less than 250 millis: but was " + _command1.getDelayBeforeExecution(),
            _command1.getDelayBeforeExecution() < 250);
    assertTrue("The delay before execution of command 2 should be greater than 250 millis: but was " + _command2.getDelayBeforeExecution(),
            _command2.getDelayBeforeExecution() >= 250);
    assertTrue("The delay before execution of command 3 should be greater than 250 millis: but was " + _command3.getDelayBeforeExecution(),
            _command3.getDelayBeforeExecution() >= 250);
  }
  
  public void testSubmit_ExplicitThrottling() throws Exception {
    _executor.setThrottlingExplicit(true);
    Future future1 = _executor.submit(new CommandAdapter(_command1));
    Future future2 = _executor.submit(new CommandAdapter(_command2));
    Future future3 = _executor.submit(new CommandAdapter(_command3));
    
    future1.get(500, TimeUnit.MILLISECONDS);
    future2.get(500, TimeUnit.MILLISECONDS);
    future3.get(500, TimeUnit.MILLISECONDS);

    assertTrue("The command is not completed", _command1.isCompleted());
    assertTrue("The command is not completed", _command2.isCompleted());
    assertTrue("The command is not completed", _command3.isCompleted());
    
    assertTrue("The delay before execution of command 1 should be less than 250 millis: but was " + _command1.getDelayBeforeExecution(),
            _command1.getDelayBeforeExecution() < 250);
    assertTrue("The delay before execution of command 2 should be greater than 250 millis: but was " + _command2.getDelayBeforeExecution(),
            _command2.getDelayBeforeExecution() < 250);
    assertTrue("The delay before execution of command 3 should be greater than 250 millis: but was " + _command3.getDelayBeforeExecution(),
            _command3.getDelayBeforeExecution() < 250);
  }
  
  public void testSubmitThrottled() throws Exception {
    Future future1 = _executor.submitThrottled(new CommandAdapter(_command1));
    Future future2 = _executor.submitThrottled(new CommandAdapter(_command2));
    Future future3 = _executor.submitThrottled(new CommandAdapter(_command3));
    
    future1.get(500, TimeUnit.MILLISECONDS);
    future2.get(500, TimeUnit.MILLISECONDS);
    future3.get(500, TimeUnit.MILLISECONDS);

    assertTrue("The command is not completed", _command1.isCompleted());
    assertTrue("The command is not completed", _command2.isCompleted());
    assertTrue("The command is not completed", _command3.isCompleted());
    
    assertTrue("The delay before execution of command 1 should be less than 250 millis: but was " + _command1.getDelayBeforeExecution(),
            _command1.getDelayBeforeExecution() < 250);
    assertTrue("The delay before execution of command 2 should be greater than 250 millis: but was " + _command2.getDelayBeforeExecution(),
            _command2.getDelayBeforeExecution() >= 250);
    assertTrue("The delay before execution of command 3 should be greater than 250 millis: but was " + _command3.getDelayBeforeExecution(),
            _command3.getDelayBeforeExecution() >= 250);
  }
  
  public void testSubmitWithError() throws Exception {
    RuntimeException kaboom = new RuntimeException("KABOOM!");
    _command1.setCause(kaboom);
    Future future = _executor.submit(new CommandAdapter(_command1));
    try {
      future.get(500, TimeUnit.MILLISECONDS);
      fail("The executed command should have thrown an exception");
    } catch (ExecutionException expected) {
      assertEquals("The cause of the execution exception is not valid", kaboom, expected.getCause());
    }
    assertTrue("The command is not completed", _command1.isCompleted());
  }
  
  public void testSotoService() throws Exception {
    SotoContainer soto = new SotoContainer();
    try {
      soto.load(new File("etc/concurrent/throttledExecutor.soto.xml"));
      soto.start();
      
      ThrottledExecutorService executor = (ThrottledExecutorService) soto.lookup("executor");
      assertNotNull("The throttled executor service retrieved should not be null", executor);
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
