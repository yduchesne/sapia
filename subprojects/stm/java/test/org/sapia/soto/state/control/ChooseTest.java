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
public class ChooseTest extends TestCase {
  public ChooseTest(String name) {
    super(name);
  }

  public void testFirstCase() throws Exception {
    Choose choose = new Choose();
    TestStep step1 = new TestStep(true);
    TestStep step2 = new TestStep(true);
    Choose.When case1 = choose.createWhen();
    case1.setTest("key == 'value'");
    case1.addExecutable(step1);

    Choose.Otherwise other = choose.createOtherwise();
    other.addExecutable(step2);

    ContextImpl ctx;
    Result st = new Result(new StateMachine(), ctx = new ContextImpl());
    Scope sc = new MapScope();
    sc.putVal("key", "value");
    ctx.addScope("test", sc);
    choose.execute(st);
    super.assertTrue(step1.exec);
    super.assertTrue(!step2.exec);
  }

  public void testSecondCase() throws Exception {
    Choose choose = new Choose();
    TestStep step1 = new TestStep(true);
    TestStep step2 = new TestStep(true);
    TestStep step3 = new TestStep(true);
    Choose.When case1 = choose.createWhen();
    case1.setTest("key == 'foo'");
    case1.addExecutable(step1);

    Choose.When case2 = choose.createWhen();
    case2.setTest("key == 'value'");
    case2.addExecutable(step2);

    Choose.Otherwise other = choose.createOtherwise();
    other.addExecutable(step3);

    ContextImpl ctx;
    Result st = new Result(new StateMachine(), ctx = new ContextImpl());
    Scope sc = new MapScope();
    sc.putVal("key", "value");
    ctx.addScope("test", sc);
    choose.execute(st);
    super.assertTrue(!step1.exec);
    super.assertTrue(step2.exec);
    super.assertTrue(!step3.exec);
  }

  public void testOtherwise() throws Exception {
    Choose choose = new Choose();
    TestStep step1 = new TestStep(true);
    TestStep step2 = new TestStep(true);
    TestStep step3 = new TestStep(true);
    Choose.When case1 = choose.createWhen();
    case1.setTest("key == 'foo'");
    case1.addExecutable(step1);

    Choose.When case2 = choose.createWhen();
    case2.setTest("key == 'bar'");
    case2.addExecutable(step2);

    Choose.Otherwise other = choose.createOtherwise();
    other.addExecutable(step3);

    ContextImpl ctx;
    Result st = new Result(new StateMachine(), ctx = new ContextImpl());
    Scope sc = new MapScope();
    sc.putVal("key", "value");
    ctx.addScope("test", sc);
    choose.execute(st);
    super.assertTrue(!step1.exec);
    super.assertTrue(!step2.exec);
    super.assertTrue(step3.exec);
  }
}
