package org.sapia.soto.state.config;

import junit.framework.TestCase;

import org.sapia.soto.SotoContainer;
import org.sapia.soto.state.ContextImpl;
import org.sapia.soto.state.ErrorHandler;
import org.sapia.soto.state.Module;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.StateAccessException;
import org.sapia.soto.state.StateMachine;
import org.sapia.soto.state.StateRef;
import org.sapia.soto.state.StepState;
import org.sapia.soto.state.TestStep;

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
public class GlobalsTest extends TestCase {
  /**
   * @param arg0
   */
  public GlobalsTest(String arg0) {
    super(arg0);
  }

  public void testApplyRestriction() {
    Globals g = new Globals();
    g.createPrivate().createInclude().setPattern("do**");

    TestState priv = new TestState(true);
    priv.setId("doFoo");

    TestState pub = new TestState(true);
    pub.setId("Foo");
    super.assertTrue("State should not be public", !g.applyRestriction(priv, true));
    super.assertTrue("State should be public", g.applyRestriction(pub, true));
  }

  public void testApplySteps() throws Exception {
    SotoContainer cont = new SotoContainer();
    StateMachine stm1 = new StateMachine();
    Globals g = new Globals();
    StateSet set = g.createPreExec();
    StatePattern pattern = set.createInclude();
    pattern.setPattern("view**");

    TestState view1 = new TestState(true);
    view1.setId("view1");

    TestStep bar = new TestStep(true);
    set.addStep(bar);

    stm1.addState(view1);

    StateMachine stm2 = new StateMachine();
    TestState view2 = new TestState(true);
    view2.setId("view2");
    stm2.addState(view2);
    Module mod1 = new Module();
    mod1.setInheritGlobals(true);
    mod1.setEnv(cont.toEnv());
    mod1.setInheritGlobals(true);
    mod1.setInheritModules(true);
    mod1.setStateMachine(stm2);

    StateMachine stm3 = new StateMachine();
    TestState view3 = new TestState(true);
    view3.setId("view3");
    stm3.addState(view3);
    Module mod2 = new Module();
    mod2.setInheritGlobals(true);
    mod2.setEnv(cont.toEnv());
    mod2.setInheritGlobals(true);
    mod2.setInheritModules(true);
    mod2.setStateMachine(stm3);

    stm1.addModule(mod1);
    stm2.addModule(mod2);
    stm1.addGlobals(g);
    stm1.init();

    stm1.execute("view1", new ContextImpl());
    super.assertTrue(view1.exec);
    super.assertTrue(bar.exec);
    bar.exec = false;

    stm1.execute("view2", new ContextImpl());
    super.assertTrue(view2.exec);
    super.assertTrue(bar.exec);
    bar.exec = false;

    stm1.execute("view3", new ContextImpl());
    super.assertTrue(view2.exec);
    super.assertTrue(bar.exec);
    bar.exec = false;

  }

  public void testIllegalAccess() throws Exception {
    Globals g = new Globals();
    g.createPrivate().createInclude().setPattern("do**");

    TestState priv = new TestState(true);
    priv.setId("doFoo");

    StepState pub = new StepState();
    pub.setId("Foo");

    StateRef ref = new StateRef();
    ref.setId("doFoo");
    ref.onCreate();    
    pub.addExecutable(ref);

    StateMachine stm = new StateMachine();
    stm.addGlobals(g);
    stm.addState(priv);
    stm.addState(pub);
    stm.init();
    stm.execute("Foo", new ContextImpl());
    super.assertTrue(priv.exec);

    try {
      stm.execute("doFoo", new ContextImpl());
      throw new Exception("Private state should not have been executed");
    } catch(StateAccessException e) {
      // ok
    }
  }

  public void testErrorHandler() throws Exception {
    StateMachine stm = new StateMachine();
    Globals g = new Globals();
    NullErrorHandler handler1 = new NullErrorHandler(false);
    NullErrorHandler handler2 = new NullErrorHandler(true);
    NullErrorHandler handler3 = new NullErrorHandler(true);
    g.createErrorHandlers().addHandleError(handler1);
    g.createErrorHandlers().addHandleError(handler2);
    g.createErrorHandlers().addHandleError(handler3);
    stm.addGlobals(g);
    stm.init();
    stm.execute("someState", new ContextImpl());
    super.assertTrue(handler1.called);
    super.assertTrue(!handler1.handled);
    super.assertTrue(handler2.called);
    super.assertTrue(handler2.handled);
    super.assertTrue(!handler3.called);
    super.assertTrue(!handler3.handled);

  }

  public void testEnterNoError() throws Exception {
    SotoContainer cont = new SotoContainer();
    
    StateMachine stm = new StateMachine();
    Globals g = new Globals();
    TestStep enter = new TestStep(true);
    g.createEnter().addExecutable(enter);
    stm.addGlobals(g);
    TestState st = new TestState(true);
    st.setId("test");
    stm.addState(st);
    
    StateMachine stm2 = new StateMachine();
    TestState st2 = new TestState(true);
    st2.setId("test2");
    stm2.addState(st2);
    Module mod1 = new Module();
    mod1.setInheritGlobals(true);
    mod1.setEnv(cont.toEnv());
    mod1.setInheritGlobals(true);
    mod1.setInheritModules(true);
    mod1.setStateMachine(stm2);
    stm.addModule(mod1);
    
    stm.init();    
    stm.execute("test", new ContextImpl());
    super.assertTrue(enter.exec);
    super.assertTrue(st.exec);
    stm.execute("test2", new ContextImpl());
    super.assertEquals(2, enter.execCount);
    super.assertTrue(st2.exec);
    
    
    
  }

  public void testEnterError() throws Exception {
    StateMachine stm = new StateMachine();
    Globals g = new Globals();
    TestStep enter = new TestStep(false);
    g.createEnter().addExecutable(enter);
    stm.addGlobals(g);
    TestState st = new TestState(true);
    st.setId("test");
    stm.addState(st);
    stm.init();
    try {
      stm.execute("test", new ContextImpl());
    } catch(Exception e) {
    }
    super.assertTrue(enter.exec);
    super.assertTrue(!st.exec);
  }

  public void testExitNoError() throws Exception {
    StateMachine stm = new StateMachine();
    Globals g = new Globals();
    TestStep exit = new TestStep(true);
    g.createExit().addExecutable(exit);
    stm.addGlobals(g);
    TestState st = new TestState(true);
    st.setId("test");
    stm.addState(st);
    stm.init();
    stm.execute("test", new ContextImpl());
    super.assertTrue(exit.exec);
    super.assertTrue(st.exec);
  }

  public void testExitError() throws Exception {
    StateMachine stm = new StateMachine();
    Globals g = new Globals();
    TestStep exit = new TestStep(true);
    g.createExit().addExecutable(exit);
    stm.addGlobals(g);
    TestState st = new TestState(false);
    st.setId("test");
    stm.addState(st);
    stm.init();
    try {
      stm.execute("test", new ContextImpl());
    } catch(Exception e) {
    }
    super.assertTrue(!exit.exec);
    super.assertTrue(st.exec);
  }

  static class NullErrorHandler implements ErrorHandler {

    boolean called, handle, handled;

    NullErrorHandler(boolean handle) {
      this.handle = handle;
    }

    /**
     * @see org.sapia.soto.state.ErrorHandler#handle(org.sapia.soto.state.Result)
     */
    public boolean handle(Result result) {
      called = true;
      if(handle) {
        handled = true;
      }
      return handle;
    }
  }
}
