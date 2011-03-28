/*
 * ChooseTest.java
 * JUnit based test
 *
 * Created on June 1, 2005, 7:55 AM
 */

package org.sapia.soto.state.xml.xpath;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.sapia.soto.state.ContextImpl;
import org.sapia.soto.state.StateMachine;
import org.sapia.soto.state.StepState;
import org.sapia.soto.state.TestStep;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 *
 * @author yduchesne
 */
public class ChooseTest extends TestCase {
  
  StateMachine _stm = new StateMachine();
  DocumentBuilderFactory _factory;
  Document _doc;
  
  public ChooseTest(String testName) {
    super(testName);
  }

  protected void setUp() throws Exception {
    _factory = DocumentBuilderFactory.newInstance();
    _factory.setNamespaceAware(true);
    _doc = _factory.newDocumentBuilder().newDocument();    
    Element root = _doc.createElement("root");
    Element foo = _doc.createElement("foo");
    Element bar = _doc.createElement("bar");
    root.appendChild(foo);
    foo.appendChild(bar);
    _doc.appendChild(root);
  }
  

  protected void tearDown() throws Exception {
  }


  /**
   * Test of execute method, of class org.sapia.soto.state.xml.xpath.Choose.
   */
  public void testWhen() throws Exception{
    Choose choose = new Choose();
    
    Choose.When w1 = choose.createWhen();
    w1.setXpath("root/sna/fu");
    TestStep step1 = new TestStep(true);
    w1.addExecutable(step1);
    
    Choose.When w2 = choose.createWhen();
    w2.setXpath("root/foo/bar");
    TestStep step2 = new TestStep(true);
    w2.addExecutable(step2);    
    
    ContextImpl ctx = new ContextImpl();
    ctx.push(_doc);
    StepState state = new StepState();
    state.setId("test");
    state.addExecutable(choose);
    _stm.addState(state);
    _stm.init();
    _stm.execute("test", ctx);
    super.assertTrue(!step1.exec);    
    super.assertTrue(step2.exec);        
  }
}
