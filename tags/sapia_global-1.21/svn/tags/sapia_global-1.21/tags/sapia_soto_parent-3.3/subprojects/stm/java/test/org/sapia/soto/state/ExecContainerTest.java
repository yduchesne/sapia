package org.sapia.soto.state;

import junit.framework.TestCase;

/**
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2003 <a
 *         href="http://www.sapia-oss.org">Sapia Open Source Software </a>. All
 *         Rights Reserved.</dd>
 *         </dt>
 *         <dt><b>License: </b>
 *         <dd>Read the license.txt file of the jar or visit the <a
 *         href="http://www.sapia-oss.org/license.html">license page </a> at the
 *         Sapia OSS web site</dd>
 *         </dt>
 *         </dl>
 */
public class ExecContainerTest extends TestCase {
  /**
   *  
   */
  public ExecContainerTest(String name) {
    super(name);
  }

  public void testAddExecutable() {
    Executable exec = new Executable() {
      public void execute(Result st) {
      }
    };

    ExecContainer container = new ExecContainer();
    container.addExecutable(exec);
    super.assertEquals(1, container._execs.size());
  }

  public void testExecSuccess() {
    TestExecContainer cont = new TestExecContainer();
    TestStep step1 = new TestStep(true);
    TestStep step2 = new TestStep(true);
    cont.addExecutable(step1);
    cont.addExecutable(step2);
    cont.execute(new Result(new StateMachine(), new ContextImpl()));
    super.assertTrue(cont.success);
    super.assertTrue(!cont.error);
    super.assertTrue(step1.exec);
    super.assertTrue(step2.exec);
  }

  public void testExecError() {
    TestExecContainer cont = new TestExecContainer();
    TestStep step1 = new TestStep(false);
    TestStep step2 = new TestStep(true);
    cont.addExecutable(step1);
    cont.addExecutable(step2);
    cont.execute(new Result(new StateMachine(), new ContextImpl()));
    super.assertTrue(!cont.success);
    super.assertTrue(cont.error);
    super.assertTrue(step1.exec);
    super.assertTrue(!step2.exec);
  }

  static class TestExecContainer extends ExecContainer {
    boolean success;
    boolean error;

    /**
     * @see org.sapia.soto.flow.ExecContainer#handleError(org.sapia.soto.flow.State)
     */
    protected void handleError(Result st) {
      error = true;
    }

    /**
     * @see org.sapia.soto.flow.ExecContainer#handleSuccess(org.sapia.soto.flow.State)
     */
    protected void handleSuccess(Result st) {
      success = true;
    }
  }
}
