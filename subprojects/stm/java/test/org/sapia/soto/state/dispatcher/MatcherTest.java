package org.sapia.soto.state.dispatcher;

import org.sapia.soto.state.Context;
import org.sapia.soto.state.ContextImpl;
import org.sapia.soto.state.Module;
import org.sapia.soto.state.StateMachine;
import org.sapia.soto.state.StateMachineService;
import org.sapia.soto.state.TestState;

import junit.framework.TestCase;

/**
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class MatcherTest extends TestCase {

  public MatcherTest(String name) {
    super(name);
  }

  public void testMatchesState() throws Exception {
    StateMachine stm = new StateMachine();
    TestState state = new TestState(true);
    state.setId("foo");
    stm.addState(state);
    StateMachineService svc = new StateMachineService();
    svc.addStateMachine(stm);
    svc.init();
    svc.start();
    Matcher matcher = new Matcher();
    matcher.setPattern("images/*.gif");
    matcher.setTarget("foo");
    matcher.setStateMachine(svc);
    Context ctx = new ContextImpl();
    matcher.matches("images/someImage.gif", ctx);
    super.assertTrue("State not executed", state.exec);
  }

  public void testMatchesWildcard() throws Exception {
    StateMachine stm = new StateMachine();
    TestState state = new TestState(true);
    state.setId("foo");
    stm.addState(state);
    StateMachineService svc = new StateMachineService();
    svc.addStateMachine(stm);
    svc.init();
    svc.start();
    Matcher matcher = new Matcher();
    matcher.setPattern("images/*.gif");
    matcher.setTarget("{1}");
    matcher.setStateMachine(svc);
    Context ctx = new ContextImpl();
    matcher.matches("images/foo.gif", ctx);
    super.assertTrue("State not executed", state.exec);
  }

  public void testMatchesModule() throws Exception {
    StateMachine stm = new StateMachine();
    TestState state = new TestState(true);
    state.setId("foo");
    StateMachine child = new StateMachine();
    child.addState(state);
    Module module = new Module();
    module.setName("someModule");

    module.handleObject("stm", child);
    stm.addModule(module);
    StateMachineService svc = new StateMachineService();
    svc.addStateMachine(stm);
    svc.init();
    svc.start();
    Matcher matcher = new Matcher();
    matcher.setPattern("images/*/*");
    matcher.setTarget("{1}/{2}");
    matcher.setStateMachine(svc);
    Context ctx = new ContextImpl();
    matcher.matches("images/someModule/foo", ctx);
    super.assertTrue("State not executed", state.exec);
  }

}
