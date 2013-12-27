package org.sapia.soto.state;

import junit.framework.TestCase;

import org.sapia.soto.state.config.StateInterceptor;
import org.sapia.util.xml.confix.ConfigurationException;

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
public class StateMachineTest extends TestCase {
  /**
   *
   */
  public StateMachineTest(String name) {
    super(name);
  }
  
  public void testAddGetFlow() throws Exception {
    StateMachine engine = new StateMachine();
    TestState main = new TestState(true);
    main.setId("main");
    
    TestState err = new TestState(true);
    err.setId("err");
    
    TestState succ = new TestState(true);
    succ.setId("succ");
    
    TestState dupl = new TestState(true);
    dupl.setId("main");
    engine.addState(main);
    engine.addState(err);
    engine.addState(succ);
    
    try {
      engine.addState(dupl);
      throw new Exception("Flow with duplicate ID should not have been added");
    } catch(ConfigurationException e) {
      //ok
    }
    
    engine.init();
    super.assertEquals(3, engine.getStmContext()._states.size());
    
    super.assertTrue(engine.getState("main") != null);
    super.assertTrue(engine.getState("foo") == null);
  }
  
  public void testExecuteSuccess() throws Exception {
    StateMachine engine = new StateMachine();
    TestState main = new TestState(true);
    main.setId("main");
    main.setSuccess("succ");
    main.setError("err");
    
    TestState err = new TestState(true);
    err.setId("err");
    
    TestState succ = new TestState(true);
    succ.setId("succ");
    engine.addState(main);
    engine.addState(err);
    engine.addState(succ);
    engine.init();
    engine.execute("main", new ContextImpl());
    super.assertTrue(main.exec);
    super.assertTrue(succ.exec);
    super.assertTrue(!err.exec);
  }
  
  public void testExecuteFailure() throws Exception {
    StateMachine engine = new StateMachine();
    TestState main = new TestState(false);
    main.setId("main");
    main.setSuccess("succ");
    main.setError("err");
    
    TestState err = new TestState(true);
    err.setId("err");
    
    TestState succ = new TestState(true);
    succ.setId("succ");
    engine.addState(main);
    engine.addState(err);
    engine.addState(succ);
    engine.init();
    engine.execute("main", new ContextImpl());
    super.assertTrue(main.exec);
    super.assertTrue(!succ.exec);
    super.assertTrue(err.exec);
  }
  
  public void testMerge() throws Exception {
    StateMachine engine1 = new StateMachine();
    TestState f1 = new TestState(false);
    f1.setId("main");
    engine1.addState(f1);
    
    StateMachine engine2 = new StateMachine();
    TestState f2 = new TestState(false);
    f2.setId("main");
    engine2.addState(f2);
    
    try {
      engine1.merge(engine2);
      throw new Exception("Duplicate should not have been merged");
    } catch(ConfigurationException e) {
      // ok
    }
    
    engine2 = new StateMachine();
    f2 = new TestState(false);
    f2.setId("second");
    engine2.addState(f2);
    engine1.merge(engine2);
    super.assertTrue(engine1.getState("main") != null);
    super.assertTrue(engine1.getState("second") != null);
  }
  
  public void testError() throws Exception {
    StateMachine engine = new StateMachine();
    engine.addState(new TestErrFlow("err", false));
    
    try {
      engine.init();
      engine.execute("err", new ContextImpl());
      throw new Exception("Engine should have thrown a FlowExecException");
    } catch(StateExecException e) {
      // ok
    }
    
    engine = new StateMachine();
    engine.addState(new TestErrFlow("err", true));
    engine.init();
    engine.execute("err", new ContextImpl());
  }
  
  public void testFlowListener() throws Exception {
    StateMachine engine = new StateMachine();
    TestFlowListener listener = new TestFlowListener();
    engine.addStateExecListener(listener);
    engine.addState(new TestErrFlow("err", true));
    engine.init();
    engine.execute("err", new ContextImpl());
    super.assertTrue(listener.pre);
    super.assertTrue(listener.err);
    super.assertTrue(!listener.post);
    
    engine = new StateMachine();
    
    TestState tf = new TestState(true);
    tf.setId("state");
    listener = new TestFlowListener();
    engine.addStateExecListener(listener);
    engine.addState(tf);
    engine.init();
    engine.execute("state", new ContextImpl());
    super.assertTrue(listener.pre);
    super.assertTrue(!listener.err);
    super.assertTrue(listener.post);
  }
  
  public void testStateDecorator() throws Exception {
    StateMachine engine = new StateMachine();
    TestState pre1 = new TestState(true);
    pre1.setId("pre1");
    
    TestState pre2 = new TestState(true);
    pre2.setId("pre2");
    
    TestState post1 = new TestState(true);
    post1.setId("post1");
    
    TestState post2 = new TestState(true);
    post2.setId("post2");
    engine.addState(pre1);
    engine.addState(pre2);
    engine.addState(post1);
    engine.addState(post2);
    
    TestState main = new TestState(true);
    main.setId("main");
    
    StateInterceptor dec = new StateInterceptor();
    dec.setPreExec("pre1, pre2");
    dec.setPostExec("post1, post2");
    dec.handleObject(null, main);
    engine.addState(dec);
    engine.init();
    engine.execute("main", new ContextImpl());
    super.assertTrue(pre1.exec);
    super.assertTrue(pre2.exec);
    super.assertTrue(main.exec);
    super.assertTrue(post1.exec);
    super.assertTrue(post2.exec);
  }
  
  public void testAddModule() throws Exception {
    StateMachine stm = new StateMachine();
    Module someModule = new Module();
    StateMachine stm2 = new StateMachine();
    someModule.setStateMachine(stm2);
    TestState someState = new TestState(true);
    someState.setId("someState");
    stm2.addState(someState);
    stm.addModule(someModule);
    stm.init();
    stm.execute("someState", new ContextImpl());
  }
  
  public void testNestedModule() throws Exception {
    StateMachine stm = new StateMachine();
    
    Module module1 = new Module();
    module1.setName("mod1");
    StateMachine stm1 = new StateMachine();
    module1.setStateMachine(stm1);
    
    Module module2 = new Module();
    module2.setName("mod2");
    StateMachine stm2 = new StateMachine();
    module2.setStateMachine(stm2);
    
    TestState someState = new TestState(true);
    someState.setId("someState");
    stm2.addState(someState);
    
    stm.addModule(module1);
    stm1.addModule(module2);
    stm.init();
    stm.execute("someState", "mod1/mod2", new ContextImpl());
    
    try {
      stm.execute("someState", "mod1", new ContextImpl());
      fail("Should not have executed state");
    } catch(Exception e) {
      //ok
    }
    
  }
  
  public void testAnonymousNestedModule() throws Exception {
    StateMachine stm = new StateMachine();
    
    Module module1 = new Module();
    module1.setName("mod1");
    StateMachine stm1 = new StateMachine();
    module1.setStateMachine(stm1);
    
    Module module2 = new Module();
    StateMachine stm2 = new StateMachine();
    module2.setStateMachine(stm2);
    
    Module module3 = new Module();
    module3.setName("mod3");
    StateMachine stm3 = new StateMachine();
    module3.setStateMachine(stm3);
    
    TestState someState = new TestState(true);
    someState.setId("someState");
    stm3.addState(someState);
    
    stm.addModule(module1);
    stm1.addModule(module2);
    stm2.addModule(module3);
    stm.init();
    stm.execute(StatePath.parse("mod1/mod3/someState"), new ContextImpl());
    
  }
  
  public void testNestedModuleParentRedirect() throws Exception {
    StateMachine stm = new StateMachine();
    
    Module module1 = new Module();
    module1.setName("mod1");
    StateMachine stm1 = new StateMachine();
    module1.setStateMachine(stm1);
    
    Module module2 = new Module();
    module2.setName("mod2");
    StateMachine stm2 = new StateMachine();
    module2.setStateMachine(stm2);
    
    Module module3 = new Module();
    module3.setName("mod3");
    module3.setInheritModules(true);
    StateMachine stm3 = new StateMachine();
    module3.setStateMachine(stm3);
    
    TestState state1 = new TestState(true);
    state1.setId("state1");
    stm1.addState(state1);
    
    TestState state2 = new TestState(true);
    state2.setId("state2");
    stm2.addState(state2);
    
    StateRef stateRef = new StateRef();
    stateRef.setId("state1");
    stateRef.setModule("mod1");
    stateRef.onCreate();
    StepState state3 = new StepState();
    state3.setId("state3");
    state3.addExecutable(stateRef);
    stm3.addState(state3);
    
    stm.addModule(module1);
    stm1.addModule(module2);
    stm.addModule(module3);
    stm.init();
    stm.execute("state3", "mod3", new ContextImpl());
    
  }
  
  public void testNestedModulePathRedirect() throws Exception {
    StateMachine stm = new StateMachine();
    
    Module module1 = new Module();
    module1.setName("mod1");
    StateMachine stm1 = new StateMachine();
    module1.setStateMachine(stm1);
    
    Module module2 = new Module();
    module2.setName("mod2");
    StateMachine stm2 = new StateMachine();
    module2.setStateMachine(stm2);
    
    Module module3 = new Module();
    module3.setName("mod3");
    module3.setInheritModules(true);
    StateMachine stm3 = new StateMachine();
    module3.setStateMachine(stm3);
    
    TestState state1 = new TestState(true);
    state1.setId("state1");
    stm1.addState(state1);
    
    
    StateRef stateRef = new StateRef();
    stateRef.setId("@/@/state1");
    stateRef.onCreate();
    StepState state3 = new StepState();
    state3.setId("state3");
    state3.addExecutable(stateRef);
    stm3.addState(state3);
    
    stm.addModule(module1);
    stm1.addModule(module2);
    stm2.addModule(module3);
    stm.init();
    stm.execute(StatePath.parse("mod1/mod2/mod3/state3"), new ContextImpl());
    super.assertTrue(state1.exec);
    
  }
  
  public void testNestedAnonymousModulePathRedirect() throws Exception {
    StateMachine stm = new StateMachine();
    
    Module module1 = new Module();
    module1.setName("mod1");
    StateMachine stm1 = new StateMachine();
    module1.setStateMachine(stm1);
    
    Module module2 = new Module();
    StateMachine stm2 = new StateMachine();
    module2.setStateMachine(stm2);
    
    Module module3 = new Module();
    module3.setName("mod3");
    module3.setInheritModules(true);
    StateMachine stm3 = new StateMachine();
    module3.setStateMachine(stm3);
    
    TestState state1 = new TestState(true);
    state1.setId("state1");
    stm1.addState(state1);
    
    
    StateRef stateRef = new StateRef();
    stateRef.setId("@/state1");
    stateRef.onCreate();
    StepState state3 = new StepState();
    state3.setId("state3");
    state3.addExecutable(stateRef);
    stm3.addState(state3);
    
    stm.addModule(module1);
    stm1.addModule(module2);
    stm2.addModule(module3);
    stm.init();
    stm.execute(StatePath.parse("mod1/mod3/state3"), new ContextImpl());
    super.assertTrue(state1.exec);
    
  }
}
