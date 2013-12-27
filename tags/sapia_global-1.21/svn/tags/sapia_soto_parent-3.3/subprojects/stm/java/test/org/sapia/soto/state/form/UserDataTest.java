/*
 * UserDataTest.java
 * JUnit based test
 *
 * Created on September 3, 2005, 8:09 AM
 */

package org.sapia.soto.state.form;

import junit.framework.*;
import java.util.HashMap;
import java.util.Map;
import org.sapia.soto.state.ContextImpl;
import org.sapia.soto.state.MapScope;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.AbstractState;
import org.sapia.soto.state.form.UserData;
import org.sapia.soto.state.StateExecException;
import org.sapia.soto.state.StateMachine;
import org.sapia.soto.state.StatePath;

/**
 *
 * @author yduchesne
 */
public class UserDataTest extends TestCase {
  
  public UserDataTest(String testName) {
    super(testName);
  }

  /**
   * Test of performGoto method, of class org.sapia.soto.state.form.UserData.
   */
  public void testPerformGotoWithReturn() throws Exception{
    StateMachine stm = new StateMachine();
    
    GotoState gs = new GotoState();
    gs.setId("goto");
    gs.targetState = StatePath.parse("target");
    gs.returnState = StatePath.parse("return");
    
    TargetState ts = new TargetState();
    ts.setId("target");
    ts.returnValue = "value";
    
    ReturnState rs = new ReturnState();
    rs.setId("return");
    
    
    stm.addState(gs);
    stm.addState(ts);
    stm.addState(rs);
    
    ContextImpl ctx = new ContextImpl();
    ctx.addScope("session", new MapScope());
    ctx.put("User", new UserData(), "session");
    stm.init();
    stm.execute("goto", ctx);
    
    super.assertTrue(gs.exec);
    super.assertTrue(ts.exec);
    super.assertTrue(rs.exec);
    super.assertEquals("value", rs.value);
  }
  
  /**
   * Test of performGoto method, of class org.sapia.soto.state.form.UserData.
   */
  public void testPerformGotoWithoutReturn() throws Exception{
    StateMachine stm = new StateMachine();
    
    GotoState gs = new GotoState();
    gs.setId("goto");
    gs.targetState = StatePath.parse("target");
    
    TargetState ts = new TargetState();
    ts.setId("target");
    ts.returnState = StatePath.parse("return");
    ts.returnValue = "value";
    
    ReturnState rs = new ReturnState();
    rs.setId("return");
    
    stm.addState(gs);
    stm.addState(ts);
    stm.addState(rs);
    
    ContextImpl ctx = new ContextImpl();
    ctx.addScope("session", new MapScope());
    ctx.put("User", new UserData(), "session");
    stm.init();
    stm.execute("goto", ctx);
    
    super.assertTrue(gs.exec);
    super.assertTrue(ts.exec);
    super.assertTrue(rs.exec);
    super.assertEquals("value", rs.value);    
  }  

  /**
   * Test of clearForm method, of class org.sapia.soto.state.form.UserData.
   */
  public void testClearForm() {
    UserData user = new UserData();
    user.createForm();
    user.clearForm();
    super.assertTrue(user.currentForm() == null);
  }

  /**
   * Test of currentForm method, of class org.sapia.soto.state.form.UserData.
   */
  public void testCurrentForm() {
    UserData user = new UserData();
    Form f = user.createForm();
    super.assertEquals(f, user.currentForm());    
  }

  /**
   * Test of put method, of class org.sapia.soto.state.form.UserData.
   */
  public void testPutGet() {
    UserData user = new UserData();
    Form f = user.createForm();
    f.put("key", "val");
    super.assertEquals("val", user.currentForm().get("key"));    
  }
  
  public static class GotoState extends AbstractState{
    
    StatePath targetState;
    StatePath returnState;
    boolean exec;
    
    public void execute(Result res){
      UserData data = (UserData)res.getContext().get("User");
      exec = true;
      try{
        if(returnState != null){
          data.performGoto(res, targetState, returnState);
        }
        else{
          data.performGoto(res, targetState);
        }
      }catch(Exception e){
        res.error(e);
      }
    }
  }
  
  public static class TargetState extends AbstractState{
    
    StatePath returnState;
    String returnValue;
    boolean exec;
    
    public void execute(Result res){
      UserData data = (UserData)res.getContext().get("User");
      exec = true;
      res.getContext().push(returnValue);
      try{
        if(returnState != null){
          data.performReturn(res, returnState);
        }
        else{
          data.performReturn(res);
        }
      }catch(Exception e){
        res.error(e);
      }        
    }
  }  
  
  public static class ReturnState extends AbstractState{
    
    boolean exec;
    String value;
    
    public void execute(Result res){
      exec = true;
      value = (String)res.getContext().currentObject();
    }
  }    
  
}
