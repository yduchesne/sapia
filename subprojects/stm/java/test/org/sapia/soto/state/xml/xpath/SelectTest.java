/*
 * SelectTest.java
 * JUnit based test
 *
 * Created on June 1, 2005, 8:10 AM
 */

package org.sapia.soto.state.xml.xpath;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.sapia.soto.state.ContextImpl;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.StateMachine;
import org.sapia.soto.state.Step;
import org.sapia.soto.state.StepState;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author yduchesne
 */
public class SelectTest extends TestCase {
  
  StateMachine _stm = new StateMachine();
  DocumentBuilderFactory _factory;
  Document _doc;
  
  public SelectTest(String testName) {
    super(testName);
  }

  protected void setUp() throws Exception {
    _factory = DocumentBuilderFactory.newInstance();
    _factory.setNamespaceAware(true);
    DocumentBuilder builder = _factory.newDocumentBuilder();
    _doc = builder.newDocument();    
    Element root = _doc.createElement("root");
    Element foo = _doc.createElement("foo");
    Element bar = _doc.createElement("bar");
    root.appendChild(foo);    
    foo.appendChild(bar);
    bar.appendChild(_doc.createCDATASection("test"));
    _doc.appendChild(root);
  }

  protected void tearDown() throws Exception {
  }

  /**
   * Test of execute method, of class org.sapia.soto.state.xml.xpath.Select.
   */
  public void testExecute() throws Exception{
    Select select = new Select();
    select.setXpath("root/foo/bar");
    SelectStep step = new SelectStep();
    select.addExecutable(step);
    ContextImpl ctx = new ContextImpl();
    ctx.push(_doc);
    StepState state = new StepState();
    state.setId("test");
    state.addExecutable(select);
    _stm.addState(state);
    _stm.init();
    _stm.execute("test", ctx);
    super.assertTrue(step.exec);    
    super.assertTrue(step.equals);        
  }
  
  
  static class SelectStep implements Step{
    
    boolean exec, equals;
    
    public String getName(){
      return "select";
    }
    
    public void execute(Result res){
      exec = true;
      Node n = (Node)res.getContext().currentObject();
      CDATASection cdata = (CDATASection)n.getChildNodes().item(0);
      equals =  "test".equals(cdata.getData());
    }
  }
  
}
