package org.sapia.soto.state.util;

import java.util.Date;

import org.sapia.soto.state.Context;
import org.sapia.soto.state.ContextImpl;
import org.sapia.soto.state.MapScope;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.StateMachine;
import org.sapia.soto.state.StepState;
import org.sapia.soto.state.helpers.ScopeParser;
import org.sapia.soto.state.util.ActionStep.MethodDescriptor;

import junit.framework.TestCase;

public class ActionStepTest extends TestCase {

  /**
  public void testExecuteEmptyArgs() throws Exception{
    StateMachine stm = new StateMachine();
    StepState state = new StepState();
    state.setId("test");
    TestActionStep step = new TestActionStep();
    step.createMethod().setName("invokeEmpty");
    step.onCreate();
    state.addExecutable(step);
    stm.addState(state);
    stm.init();
    Context ctx = new ContextImpl();
    stm.execute("test", ctx);
    super.assertTrue(step.invoked);
  }
  
  public void testExecuteArgs() throws Exception{
    StateMachine stm = new StateMachine();
    StepState state = new StepState();
    state.setId("test");
    TestActionStep step = new TestActionStep();
    MethodDescriptor meth = step.createMethod();
    meth.addParam(new Date());
    meth.addParam("test");
    meth.addParam(new Integer(0));
    meth.setName("invoke");
    step.onCreate();
    state.addExecutable(step);
    stm.addState(state);
    stm.init();
    Context ctx = new ContextImpl();
    stm.execute("test", ctx);
    super.assertTrue(step.invoked);
  }  
  
  public void testExecuteArgsFromContext() throws Exception{
    StateMachine stm = new StateMachine();
    StepState state = new StepState();
    state.setId("test");
    TestActionStep step = new TestActionStep();
    MethodDescriptor meth = step.createMethod();
    meth.setName("invoke");
    meth.addParam(ScopeParser.parseKey("date:params"));
    meth.addParam(ScopeParser.parseKey("foo:params"));
    meth.addParam(ScopeParser.parseKey("number:params"));
    step.onCreate();
    state.addExecutable(step);
    stm.addState(state);
    stm.init();
    ContextImpl ctx = new ContextImpl();
    MapScope scope = new MapScope();
    ctx.addScope("params", scope);
    scope.put("date", new Date());
    scope.put("foo", "bar");
    scope.put("number", new Integer(0));
    stm.execute("test", ctx);
    super.assertTrue(step.invoked);
  }
  
  public void testExecuteBean() throws Exception{
    StateMachine stm = new StateMachine();
    StepState state = new StepState();
    state.setId("test");
    TestActionStep step = new TestActionStep();
    step.setClass(TestActionStep.class.getName());
    step.createMethod().setName("invokeClass");
    step.onCreate();
    state.addExecutable(step);
    stm.addState(state);
    stm.init();
    Context ctx = new ContextImpl();
    stm.execute("test", ctx);
    super.assertTrue(step.classInvoked);
  }*/
  
  public void testExecuteArgsFromStm() throws Exception{
    StateMachine stm = new StateMachine();
    StepState state = new StepState();
    state.setId("test");
    TestActionStep step = new TestActionStep();
    MethodDescriptor meth = step.createMethod();
    meth.setName("invoke2");
    meth.addParam(ScopeParser.parseKey("foo:params"));
    step.onCreate();
    state.addExecutable(step);
    stm.addState(state);
    stm.init();
    ContextImpl ctx = new ContextImpl();
    MapScope scope = new MapScope();
    ctx.addScope("params", scope);
    scope.put("foo", "bar");
    stm.execute("test", ctx);
    super.assertTrue(step.invoked);
  }  
  
  public static class TestActionStep extends ActionStep{
    
    static boolean classInvoked;
    
    boolean invoked;
    
    public void invokeEmpty(){
      invoked = true;
    }
    
    public static void invokeClass(){
      classInvoked = true;
    }
    
    public void invoke(Date d, String s, int value){
      invoked = true; 
    }
    
    public void invoke2(String s, Result res, Context ctx) throws Exception{
      if(ctx == null){
        throw new Exception("Context instance is null");
      }
      invoked = true;
    }
    
  }

}
