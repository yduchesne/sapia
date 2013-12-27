package org.sapia.soto.state;

import java.io.File;

import junit.framework.TestCase;

import org.sapia.soto.SotoContainer;
import org.sapia.soto.state.config.Globals;
import org.sapia.soto.state.config.StateSet;
import org.sapia.soto.state.config.StepContainer;

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
public class ModuleTest extends TestCase {
  public ModuleTest(String name) {
    super(name);
  }

  public void testReload() throws Exception {
    SotoContainer cont = new SotoContainer();
    File conf;
    File included;
    included = new File("etc/stm/included.xml");
    included.setLastModified(System.currentTimeMillis());
    System.setProperty("soto.debug", "true");
    cont.load(conf = new File("etc/stm/config.xml"));

    StateMachineService svc = (StateMachineService) cont.lookup("stateService");
    svc.execute("test", "test", new ContextImpl());

    included.setLastModified(1);
    Thread.sleep(500);
    svc.execute("test", "test", new ContextImpl());
  }

  public void testReloadAnonymous() throws Exception {
    SotoContainer cont = new SotoContainer();
    File conf;
    File included;
    included = new File("etc/stm/included.xml");
    included.setLastModified(System.currentTimeMillis());
    System.setProperty("soto.debug", "true");
    cont.load(conf = new File("etc/stm/configAnonymous.xml"));

    StateMachineService svc = (StateMachineService) cont.lookup("stateService");
    svc.execute("test", null, new ContextImpl());

    included.setLastModified(1);
    Thread.sleep(500);
    svc.execute("test", null, new ContextImpl());
  }
  
  public void testInheritGlobals() throws Exception{
    StateMachine stm = new StateMachine();
    
    Globals gb = new Globals();
    ExecContainer exec = gb.createEnter();
    StateSet set = gb.createPreExec();
   
    TestStep enterStep = new TestStep(true);
    exec.addExecutable(enterStep);
    stm.addGlobals(gb);
    
    TestStep exitStep = new TestStep(true);
    exec.addExecutable(exitStep);
    stm.addGlobals(gb);    
    
    TestStep preExecStep = new TestStep(true);
    exec.addExecutable(preExecStep);
    stm.addGlobals(gb);    
    set.addStep(preExecStep);
    
    TestStep postExecStep = new TestStep(true);
    exec.addExecutable(postExecStep);
    stm.addGlobals(gb);    
    set.addStep(postExecStep);    
    
    Module module1 = new Module();
    module1.setName("mod1");
    StateMachine stm1 = new StateMachine();
    module1.setStateMachine(stm1);
    module1.setInheritGlobals(true);

    Module module2 = new Module();
    module2.setName("mod2");
    StateMachine stm2 = new StateMachine();
    module2.setStateMachine(stm2);
    module2.setInheritGlobals(true);
    
    TestState state = new TestState(true);
    state.setId("test");
    stm2.addState(state);
    
    stm1.addModule(module2);
    stm.addModule(module1);
    
    stm.init();
    stm.execute(StatePath.parse("mod1/mod2/test"), new ContextImpl());
    
    super.assertTrue(state.exec);
    super.assertTrue(enterStep.exec);
    super.assertTrue(exitStep.exec);
    super.assertTrue(preExecStep.exec);
    super.assertTrue(postExecStep.exec);    
  }
  
  public void testNotInheritGlobals() throws Exception{
    StateMachine stm = new StateMachine();
    
    Globals gb = new Globals();
    ExecContainer exec = gb.createEnter();
    StateSet set = gb.createPreExec();
   
    TestStep enterStep = new TestStep(true);
    exec.addExecutable(enterStep);
    stm.addGlobals(gb);
    
    TestStep exitStep = new TestStep(true);
    exec.addExecutable(exitStep);
    stm.addGlobals(gb);    
    
    TestStep preExecStep = new TestStep(true);
    exec.addExecutable(preExecStep);
    stm.addGlobals(gb);    
    set.addStep(preExecStep);
    
    TestStep postExecStep = new TestStep(true);
    exec.addExecutable(postExecStep);
    stm.addGlobals(gb);    
    set.addStep(postExecStep);    
    
    Module module1 = new Module();
    module1.setName("mod1");
    StateMachine stm1 = new StateMachine();
    module1.setStateMachine(stm1);
    module1.setInheritGlobals(false);

    Module module2 = new Module();
    module2.setName("mod2");
    StateMachine stm2 = new StateMachine();
    module2.setStateMachine(stm2);
    module2.setInheritGlobals(false);
    
    Module module3 = new Module();
    module3.setInheritModules(true);
    StateMachine stm3 = new StateMachine();
    module3.setStateMachine(stm3);
    module3.setInheritGlobals(false);    
    
    TestState state = new TestState(true);
    state.setId("test");
    stm2.addState(state);
    
    StepState redirect = new StepState();
    redirect.setId("redirect");
    StateRef ref = new StateRef();
    ref.setId("test");
    ref.onCreate();
    redirect.addExecutable(ref);
    stm3.addState(redirect);

    stm2.addModule(module3);
    stm1.addModule(module2);
    stm.addModule(module1);
    
    
    stm.init();
    stm.execute(StatePath.parse("mod1/mod2/test"), new ContextImpl());

    super.assertTrue(state.exec);
    
    stm.execute(StatePath.parse("mod1/mod2/redirect"), new ContextImpl());

    super.assertEquals(2, state.execCount);
    
    super.assertTrue(!enterStep.exec);
    super.assertTrue(!exitStep.exec);
    super.assertTrue(!preExecStep.exec);
    super.assertTrue(!postExecStep.exec);    

  }  
}
