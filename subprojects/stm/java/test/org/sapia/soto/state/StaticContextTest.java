/*
 * StaticContextTest.java
 * JUnit based test
 *
 * Created on June 21, 2005, 8:34 AM
 */

package org.sapia.soto.state;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

/**
 *
 * @author yduchesne
 */
public class StaticContextTest extends TestCase {
  
  private StaticContext _root;
  private StaticContext _child;
  private StaticContext _anonymous;
  
  public StaticContextTest(String testName) {
    super(testName);
  }

  protected void setUp() throws Exception {
    StateMachine root = new StateMachine();
    
    Module childModule = new Module();
    StateMachine childStm = new StateMachine();    
    childModule.setName("child");    
    childModule.setStateMachine(childStm);
    root.addModule(childModule);
    
    Module anonModule = new Module();    
    StateMachine anonStm = new StateMachine();    
    anonModule.setStateMachine(anonStm);
    childStm.addModule(anonModule);
    
    root.init();
    _root = root.getStmContext();
    _child = childStm.getStmContext();
    _anonymous = anonStm.getStmContext();
    
    _child._states.put("state1", new StateHolder(new TestState(true)));
    _child._states.put("state2", new StateHolder(new TestState(true)));    
    
    
  }

  /**
   * Test of getParent method, of class org.sapia.soto.state.StaticContext.
   */
  public void testGetParent() {
    super.assertTrue(_root.getParent() == null);
    super.assertTrue(_child.getParent().getStmContext() == _root);    
    super.assertTrue(_anonymous.getParent().getStmContext() == _child);    
  }

  /**
   * Test of getStates method, of class org.sapia.soto.state.StaticContext.
   */
  public void testGetStates() {
    super.assertTrue(_child.getStates().containsKey("state1"));
    super.assertTrue(_child.getStates().containsKey("state2"));
  }

  /**
   * Test of getStateNames method, of class org.sapia.soto.state.StaticContext.
   */
  public void testGetStateNames() {
    Set expected = new HashSet();
    expected.add("state1");
    expected.add("state2");
    String[] names = _child.getStateNames();
    for(int i = 0; i < names.length; i++){
      super.assertTrue(expected.contains(names[i]));
    }
  }

  /**
   * Test of getModulePath method, of class org.sapia.soto.state.StaticContext.
   */
  public void testGetModulePath() {
    StatePath path = _child.getModulePath();
    super.assertTrue(path.hasNextPathToken());
    super.assertEquals("child", path.nextPathToken());
  }

}
