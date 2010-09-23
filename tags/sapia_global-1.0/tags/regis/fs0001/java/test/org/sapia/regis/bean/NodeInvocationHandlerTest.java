package org.sapia.regis.bean;

import java.net.Socket;

import org.sapia.regis.SessionUtil;
import org.sapia.regis.impl.NodeImpl;
import org.sapia.regis.local.LocalRegistry;

import junit.framework.TestCase;

public class NodeInvocationHandlerTest extends TestCase {
  
  public NodeInvocationHandlerTest(String name){
    super(name);
  }

  public void testGetValues(){
    LocalRegistry reg = new LocalRegistry();
    NodeImpl node = new NodeImpl(null, "");
    node.setProperty("booleanValue", "true");
    node.setProperty("intValue", "10");    
    node.setProperty("longValue", "100");
    node.setProperty("floatValue", "20.2");    
    node.setProperty("doubleValue", "200.2");
    
    NodeInvocationHandler handler = new NodeInvocationHandler(reg, node, Config.class);
    
    Config conf = (Config)BeanFactory.newBeanInstanceFor(node, Config.class, handler);
    
    super.assertEquals(true, conf.getBooleanValue());
    super.assertEquals(10, conf.getIntValue());
    super.assertEquals(100, conf.getLongValue());
    super.assertTrue(20.2f == conf.getFloatValue());    
    super.assertTrue(200.2d == conf.getDoubleValue());    
  }
  
  public void testWithSessionUtil(){
    LocalRegistry reg = new LocalRegistry();
    NodeImpl node = new NodeImpl(null, "");
    node.setProperty("booleanValue", "true");
    node.setProperty("intValue", "10");    
    node.setProperty("longValue", "100");
    node.setProperty("floatValue", "20.2");    
    node.setProperty("doubleValue", "200.2");
    
    NodeInvocationHandler handler = new NodeInvocationHandler(reg, node, Config.class);
    
    Config conf = (Config)BeanFactory.newBeanInstanceFor(node, Config.class, handler);
    
    SessionUtil.createSessionFor(conf);
    
    super.assertEquals(true, conf.getBooleanValue());
    super.assertEquals(10, conf.getIntValue());
    super.assertEquals(100, conf.getLongValue());
    super.assertTrue(20.2f == conf.getFloatValue());    
    super.assertTrue(200.2d == conf.getDoubleValue());
    
    SessionUtil.close();
    
  }
  
  public void testInvalidConfig(){
    LocalRegistry reg = new LocalRegistry();
    NodeImpl node = new NodeImpl(null, "");
    node.setProperty("socket", "1099");

    try{
      new NodeInvocationHandler(reg, node, InvalidConfig.class);
      fail("Socket property should not be processed");
    }catch(InvalidReturnTypeException e){
      //ok
    }
  }  
  
  public void testMissingProperty(){
    LocalRegistry reg = new LocalRegistry();    
    NodeImpl node = new NodeImpl(null, "");
    
    NodeInvocationHandler handler = new NodeInvocationHandler(reg, node, Config.class);
    Config conf = (Config)BeanFactory.newBeanInstanceFor(node, Config.class, handler);
    try{
      conf.getBooleanValue();
      fail("property should not be returned");
    }catch(IllegalStateException e){
      //ok
    }
  }  
  
  public static interface Config{
    
    public boolean getBooleanValue();
    
    public int getIntValue();
    
    public long getLongValue();

    public float getFloatValue();
    
    public double getDoubleValue();
    
  }
  
  public static interface InvalidConfig{

    public Socket getSocket();
    
  }  
}
