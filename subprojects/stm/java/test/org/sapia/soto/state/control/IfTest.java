package org.sapia.soto.state.control;

import junit.framework.TestCase;

import org.sapia.soto.state.ContextImpl;
import org.sapia.soto.state.MapScope;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.Scope;
import org.sapia.soto.state.StateMachine;
import org.sapia.soto.state.TestStep;

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
public class IfTest extends TestCase {
  public IfTest(String name) {
    super(name);
  }

  public void testExecTrue() throws Exception {
    If ifStep = new If();
    TestStep step1 = new TestStep(true);

    ifStep.addExecutable(step1);
    ifStep.setTest("key == 'value'");

    ContextImpl ctx;
    Result st = new Result(new StateMachine(), ctx = new ContextImpl());
    Scope sc = new MapScope();
    sc.putVal("key", "value");
    ctx.addScope("test", sc);
    ifStep.execute(st);
    super.assertTrue(step1.exec);
  }

  public void testExecFalse() throws Exception {
    If ifStep = new If();

    TestStep step1 = new TestStep(true);

    ifStep.addExecutable(step1);
    ifStep.setTest("key == 'value'");

    ContextImpl ctx;
    Result st = new Result(new StateMachine(), ctx = new ContextImpl());
    Scope sc = new MapScope();
    ctx.addScope("test", sc);
    ifStep.execute(st);
    super.assertTrue(!step1.exec);
  }
}
