package org.sapia.soto.regis;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.sapia.regis.Node;
import org.sapia.regis.Registry;
import org.sapia.resource.include.IncludeState;
import org.sapia.soto.SotoContainer;

import junit.framework.TestCase;

public class RegistryServiceTest extends TestCase {
  
  RegistryService reg;
  SotoContainer cont;

  public RegistryServiceTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
  
    cont = new SotoContainer();
    cont.load(new File("etc/regis/soto.xml"));
//    cont.load("resource:/regis/soto.xml");
    reg = (RegistryService)cont.lookup(Registry.class);
  }

 
  
  public void testConfigBean() throws Exception{
    TestRegisClient client = (TestRegisClient)cont.lookup("client");
    assertEquals("cbrown", client._cfg.getUsername());
    assertEquals("Charlie", client._cfg.getFirstName());    
    assertEquals("Brown", client._cfg.getLastName());
    assertEquals("lupus9890!", client._cfg.getPassword());    
  }
 
  public void testSingleNode() throws Exception{
    TestRegisClient client = (TestRegisClient)cont.lookup("client");
    assertEquals("cbrown", client._node.getProperty("username").asString());
    assertEquals("Charlie", client._node.getProperty("firstName").asString());    
    assertEquals("Brown", client._node.getProperty("lastName").asString());
    assertEquals("lupus9890!", client._node.getProperty("password").asString());    
  }

  public void testMultiNodes() throws Exception{
    TestRegisClient client = (TestRegisClient)cont.lookup("client");
    Collection nodes = client._nodes;
    assertEquals(1, nodes.size());
    Node node = (Node)nodes.iterator().next();
    assertEquals("cbrown", node.getProperty("username").asString());
    assertEquals("Charlie", node.getProperty("firstName").asString());    
    assertEquals("Brown", node.getProperty("lastName").asString());
    assertEquals("lupus9890!", node.getProperty("password").asString());    
  }    
  
  public void testCompositeNode() throws Exception{
    TestRegisClient client = (TestRegisClient)cont.lookup("client");
    Node node = client._compNode.getChild("aliasAccount1");
    assertEquals("cbrown", node.getProperty("username").asString());
    assertEquals("Charlie", node.getProperty("firstName").asString());    
    
    node = client._compNode.getChild("account2");
    assertEquals("dmenace", node.getProperty("username").asString());
    assertEquals("Dennis", node.getProperty("firstName").asString());    
  }      

  public void testAggregation() throws Exception{
    TestRegisClient client = (TestRegisClient)cont.lookup("client");
    assertEquals(3, client._dbServices.size());
    Iterator itr = client._dbServices.iterator();
    TestDbService first  = (TestDbService)itr.next();
    assertTrue(first.config != null);
    assertEquals("jsmith", first.getUsername());
    assertEquals("foo123", first.getPassword());    
    assertEquals("jdbc://saturn:5432/", first.getUrl());
    
    TestDbService second = (TestDbService)itr.next();
    assertTrue(second.config != null);    
    assertEquals("stiger", second.getUsername());
    assertEquals("bar123", second.getPassword());    
    assertEquals("jdbc://pluto:5432/", second.getUrl());    
    
    TestDbService third = (TestDbService)itr.next();
    assertTrue(third.config != null);    
    assertEquals("stiger", third.getUsername());
    assertEquals("bar123", third.getPassword());    
    assertEquals("jdbc://mercury:5432/", third.getUrl());    
  }    

  public void testInclude() throws Exception{
    TestDbService included = (TestDbService)cont.lookup("includedDbService");
    assertEquals("stiger", included.getUsername());
    assertEquals("bar123", included.getPassword());    
    assertEquals("jdbc://mercury:5432/", included.getUrl());    
    assertEquals("admin", included.getRole());
    assertTrue(included.isReadOnly());
  }
  
  public void testCompositeInclude() throws Exception{
    TestDbService included = (TestDbService)cont.lookup("includedCompositeDbService");
    assertEquals("jsmith", included.getUsername());
    assertEquals("foo123", included.getPassword());    
    assertEquals("jdbc://saturn:5432/", included.getUrl());    
    assertEquals("admin", included.getRole());
    assertTrue(included.isReadOnly());
  }

  public void testURI() throws Exception{
    Properties props = new Properties();
    props.load(cont.toEnv()
        .resolveResource("regis:/registry/users/backoffice/account1").getInputStream());

    assertEquals("cbrown", props.getProperty("username"));
    assertEquals("Charlie", props.getProperty("firstName"));    
    assertEquals("Brown", props.getProperty("lastName"));
    assertEquals("lupus9890!", props.getProperty("password"));    
  }
  
  public void testChoose() throws Exception{
    Map values = (Map)cont.lookup("chooseTest");
    assertEquals("test2", values.get("prop"));
  }
  
  public void testIf() throws Exception{
    Map values = (Map)cont.lookup("ifTest");
    assertEquals("test1", values.get("prop"));
  }     

}
