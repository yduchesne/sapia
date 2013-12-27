package org.sapia.soto.state.markup;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javax.xml.transform.OutputKeys;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.SerializerFactory;
import org.sapia.soto.SotoContainer;
import org.sapia.soto.state.ContextImpl;
import org.sapia.soto.state.Output;
import org.sapia.soto.state.StateMachine;
import org.sapia.soto.state.StateMachineService;
import org.sapia.soto.state.StatePath;
import org.sapia.soto.state.xml.XMLContextImpl;
import org.w3c.dom.Document;

import junit.framework.TestCase;

public class MarkupStepTest extends TestCase {
  
  StateMachine stm;
  SotoContainer container;
  public MarkupStepTest(String name){
    super(name);
  }
  
  protected void setUp() throws Exception {
    container = new SotoContainer();
    stm = new StateMachine();
  }
  public void testExecute() throws Exception{
    MarkupStep step = new MarkupStep();
    step.setEnv(container.toEnv());
    step.setId("test");
    step.setName("test");
    step.setNameInfo("test", "ts", "soto:test");
    step.handleObject("foo", "bar");
    step.onCreate();
    stm.addState(step);
    stm.init();
    TestContext ctx = new TestContext();
    stm.execute("test", ctx);
  }
  
  public void testExecuteElements() throws Exception{
    container.toEnv().getSettings().addString(MarkupSerializerFactory.SETTING_OUTPUT_ENCODE_ELEMENTS, "true");
    MarkupStep step = new MarkupStep();
    step.setEnv(container.toEnv());
    step.setId("test");
    step.setName("test");
    step.setNameInfo("test", "ts", "soto:test");
    step.handleObject("foo", "bar");
    step.onCreate();
    stm.addState(step);
    stm.init();
    TestContext ctx = new TestContext();
    stm.execute("test", ctx);
  }  
  
  public void testExecuteDefault() throws Exception{
    container.load(new File("etc/markup/main.xml")).start();
    StateMachineService stm = (StateMachineService)container.lookup("stm");
    stm.execute(StatePath.parse("htmlDoc"), new TestContext());
  }
  
  public void testExecuteSax() throws Exception{
    Properties props = new Properties();
    props.setProperty(OutputKeys.INDENT, "yes");
    props.setProperty(OutputKeys.METHOD, "xml");

    OutputFormat format = new OutputFormat("xml", "UTF-8", true);
    Serializer ser = SerializerFactory.getSerializerFactory("xml")
        .makeSerializer(System.out, format);
    
    container.getSettings().addString(
        MarkupSerializerFactory.SETTING_SERIALIZER, 
        MarkupSerializerFactory.SERIALIZER_SAX);
    container.load(new File("etc/markup/main.xml")).start();
    StateMachineService stm = (StateMachineService)container.lookup("stm");
    TestContext ctx = new TestContext();
    ctx.setContentHandler(ser.asContentHandler());
    stm.execute(StatePath.parse("htmlDoc"), ctx);
  }
  
  public void testExecuteDom() throws Exception{
    container.getSettings().addString(
        MarkupSerializerFactory.SETTING_SERIALIZER, 
        MarkupSerializerFactory.SERIALIZER_DOM);
    container.load(new File("etc/markup/main.xml")).start();
    StateMachineService stm = (StateMachineService)container.lookup("stm");
    TestContext ctx = new TestContext();
    stm.execute(StatePath.parse("htmlDoc"), ctx);
    assertTrue(ctx.currentObject() instanceof Document);
  }    
  
  class TestContext extends XMLContextImpl implements Output{
    
    OutputStream out = System.out;
    
    public OutputStream getOutputStream() throws IOException {

      return out;
    }
    
    public void setOutputStream(OutputStream os) throws IOException {
      out = os;
    }
    
  }

}
