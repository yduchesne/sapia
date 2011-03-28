/*
 * IfTest.java
 * JUnit based test
 *
 * Created on May 31, 2005, 9:42 AM
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
public class IfTest extends TestCase {
  
  StateMachine _stm = new StateMachine();
  DocumentBuilderFactory _factory;
  Document _doc;
  
  public IfTest(String testName) {
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
  

  protected void tearDown() throws Exception {}  

  public void testExecute() throws Exception{
    If ifXpath = new If();
    ifXpath.setXpath("root/foo/bar");
    TestStep step = new TestStep(true);
    ifXpath.addExecutable(step);
    ContextImpl ctx = new ContextImpl();
    ctx.push(_doc);
    StepState state = new StepState();
    state.setId("test");
    state.addExecutable(ifXpath);
    _stm.addState(state);
    _stm.init();
    _stm.execute("test", ctx);
    super.assertTrue(step.exec);
  }
  
}
