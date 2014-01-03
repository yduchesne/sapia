package org.sapia.regis.loader;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.Property;
import org.sapia.regis.RWNode;
import org.sapia.regis.RWSession;
import org.sapia.regis.Registry;
import org.sapia.regis.local.LocalRegistryFactory;

public class RegistryConfigLoaderTest extends TestCase {
  
  RegistryConfigLoader loader;
  Registry reg;

  public RegistryConfigLoaderTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    LocalRegistryFactory factory = new LocalRegistryFactory();
    reg = factory.connect(new Properties());
  }
  
  public void testCreate() throws Exception{
    RWSession session = (RWSession)reg.open();
    session.begin();    
    RWNode node = (RWNode)reg.getRoot();
    RegistryConfigLoader loader = new RegistryConfigLoader(node);
    loader.load(new File("etc/configCreateExample.xml"));
    session.commit();
    
    Node db000 = node.getChild(Path.parse("databases/000"));
    assertEquals("jsmith", db000.getProperty("username").asString());
    assertEquals("foo123", db000.getProperty("password").asString());
    assertEquals("jdbc://saturn:5432/", db000.getProperty("url").asString());    
    
    Node db001 = node.getChild(Path.parse("databases/001"));
    assertEquals("stiger", db001.getProperty("username").asString());
    assertEquals("bar123", db001.getProperty("password").asString());
    assertEquals("jdbc://pluto:5432/", db001.getProperty("url").asString());
    
    Node db002 = node.getChild(Path.parse("databases/002"));
    assertEquals("stiger", db002.getProperty("username").asString());
    assertEquals("bar123", db002.getProperty("password").asString());
    assertEquals("jdbc://mercury:5432/", db002.getProperty("url").asString());    
    
    Node account1 = node.getChild(Path.parse("users/backoffice/account1"));
    assertEquals("cbrown", account1.getProperty("username").asString());    
    assertEquals("Charlie", account1.getProperty("firstName").asString());    
    assertEquals("Brown", account1.getProperty("lastName").asString());
    assertEquals("lupus9890!", account1.getProperty("password").asString());    
    
    Node account2 = node.getChild(Path.parse("users/backoffice/account2"));
    assertEquals("dmenace", account2.getProperty("username").asString());    
    assertEquals("Dennis", account2.getProperty("firstName").asString());    
    assertEquals("Menace", account2.getProperty("lastName").asString());
    assertEquals("canis$2677", account2.getProperty("password").asString());    
  }
  
  public void testUpdate() throws Exception{
    RWSession session = (RWSession)reg.open();
    session.begin();
    RWNode node = (RWNode)reg.getRoot();
    RegistryConfigLoader loader = new RegistryConfigLoader(node);
    loader.load(new File("etc/configCreateExample.xml"));
    loader = new RegistryConfigLoader(node);
    loader.load(new File("etc/configUpdateExample.xml"));
    session.commit();
    
    Node db000 = node.getChild(Path.parse("databases/000"));
    assertTrue(db000 == null);
    
    Node db001 = node.getChild(Path.parse("databases/001"));
    assertEquals("mrspock", db001.getProperty("username").asString());
    assertEquals("bar123", db001.getProperty("password").asString());
    assertEquals("jdbc://pluto:5432/", db001.getProperty("url").asString());
    
    Node account1 = node.getChild(Path.parse("users/backoffice/account1"));
    assertEquals("cbrown", account1.getProperty("username").asString());    
    assertEquals("Charlie", account1.getProperty("firstName").asString());    
    assertEquals("Brown", account1.getProperty("lastName").asString());
    assertEquals("lupus9890!", account1.getProperty("password").asString());    
    
    Node address1 = node.getChild(Path.parse("users/backoffice/account1/address"));
    assertEquals("1234 Sesame", address1.getProperty("street").asString());    
    assertEquals("New York", address1.getProperty("city").asString());    
    assertEquals("NY", address1.getProperty("state").asString());
    assertEquals("US", address1.getProperty("country").asString());    
    
    Node account2 = node.getChild(Path.parse("users/backoffice/account2"));
    assertEquals("dmenace", account2.getProperty("username").asString());    
    assertEquals("Dennis", account2.getProperty("firstName").asString());    
    assertEquals("Menace", account2.getProperty("lastName").asString());
    assertEquals("canis$2677", account2.getProperty("password").asString());    
  }  
  
  public void testLink() throws Exception{
    RWSession session = (RWSession)reg.open();
    session.begin();
    RWNode node = (RWNode)reg.getRoot();
    RegistryConfigLoader loader = new RegistryConfigLoader(node);
    loader.load(new File("etc/configLinkExample.xml"));
    session.commit();
    Node props = node.getChild(Path.parse("properties"));
    assertEquals("value1", props.getProperty("prop1").asString());
    assertEquals("value2", props.getProperty("prop2").asString());
    assertEquals("value3", props.getProperty("prop3").asString());    
    assertEquals("globalValue", props.getProperty("globalProp").asString());    
  }
  
  public void testInclude() throws Exception{
    RWSession session = (RWSession)reg.open();
    session.begin();
    RWNode node = (RWNode)reg.getRoot();
    RegistryConfigLoader loader = new RegistryConfigLoader(node);
    loader.load(new File("etc/configCreateExample.xml"));
    session.commit();
    
    Node child = node.getChild(Path.parse("users/support"));
    Collection children = child.getChildren();
    assertEquals(4, children.size());
    assertInclude("account1", children);    
    assertInclude("account2", children);    
    assertInclude("account3", children);
    assertInclude("manager", children);    
    
    Node manager = child.getChild("manager");
    System.out.println(manager.getProperties());
    assertTrue(!manager.getProperty("username").isNull());
    assertTrue(!manager.getProperty("password").isNull());
    assertTrue(!manager.getProperty("firstName").isNull());    
    assertTrue(!manager.getProperty("lastName").isNull());    
  }
  
  public void testWhenConfig() throws Exception{
    Map params = new HashMap();
    params.put("env", "prod");
    params.put("continent", "north-america-01");    
    RWSession session = (RWSession)reg.open();
    session.begin();
    RWNode node = (RWNode)reg.getRoot();
    RegistryConfigLoader loader = new RegistryConfigLoader(node);
    loader.load(new File("etc/configConditionExample.xml"), params);
    session.commit();    
    
    Node child = node.getChild(Path.parse("databases/000"));
    assertEquals("jdbc://jupiter:5432/", child.getProperty("url").asString());
    
    child = node.getChild(Path.parse("connection"));
    assertEquals("http://www.google.com", child.getProperty("url").asString());    
  }  
  
  public void testOtherwiseConfig() throws Exception{
    Map params = new HashMap();
    params.put("env", "qa");
    RWSession session = (RWSession)reg.open();
    session.begin();
    RWNode node = (RWNode)reg.getRoot();
    RegistryConfigLoader loader = new RegistryConfigLoader(node);
    loader.load(new File("etc/configConditionExample.xml"), params);
    session.commit();    
    
    Node child = node.getChild(Path.parse("databases/000"));
    assertEquals("jdbc://saturn:5432/", child.getProperty("url").asString());
  }  
  
  public void testDefaultParamRefConfig() throws Exception{
    Map params = new HashMap();
    params.put("env", "dev");
    RWSession session = (RWSession)reg.open();
    session.begin();
    RWNode node = (RWNode)reg.getRoot();
    RegistryConfigLoader loader = new RegistryConfigLoader(node);
    loader.load(new File("etc/configConditionExample.xml"), params);
    session.commit();    
    
    Node child = node.getChild(Path.parse("databases/000"));
    assertEquals("jdbc://pluto:5432/", child.getProperty("url").asString());
  }  
  
  public void testParamRefConfig() throws Exception{
    Map params = new HashMap();
    params.put("env", "local");
    params.put("local.db.url", "jdbc://mercury:5432/");
    
    RWSession session = (RWSession)reg.open();
    session.begin();
    RWNode node = (RWNode)reg.getRoot();
    RegistryConfigLoader loader = new RegistryConfigLoader(node);
    loader.load(new File("etc/configConditionExample.xml"), params);
    session.commit();    
    
    Node child = node.getChild(Path.parse("databases/000"));
    assertEquals("jdbc://mercury:5432/", child.getProperty("url").asString());    
  }
  
  public void testLinkIncludePath() throws Exception{
    RWSession session = (RWSession)reg.open();
    session.begin();
    RWNode node = (RWNode)reg.getRoot();
    RegistryConfigLoader loader = new RegistryConfigLoader(node);
    loader.load(new File("etc/multifile1.xml"));
    loader.load(new File("etc/multifile2.xml"));    
    session.commit();
    Node child = node.getChild(Path.parse("databaseInclude/000"));
    assertEquals("000", child.getName());
    child = node.getChild(Path.parse("databaseInclude/001"));
    assertEquals("001", child.getName());
    
    child = node.getChild(Path.parse("userLink"));
    assertEquals("jsmith", child.getProperty("username").asString());

    child = node.getChild(Path.parse("userInclude/account1"));
    assertEquals("account1", child.getName());

    session.close();
  }  
  
  public void testCustomPropertyTag() throws Exception{
    RWSession session = (RWSession)reg.open();
    session.begin();
    RWNode node = (RWNode)reg.getRoot();
    RegistryConfigLoader loader = new RegistryConfigLoader(node);
    loader.load(new File("etc/customPropertyTag.regis.xml"));
    session.commit();
    
    HashMap map = new HashMap();
    map.put("username", "jsmith");
    map.put("password", "foo123");
    map.put("url", "jdbc://saturn:5432/");
    
    Node child1 = node.getChild(Path.parse("databases/000"));
    assertNode("000", map, child1);
    
    Node child2 = node.getChild(Path.parse("databases/000-custom"));
    assertNode("000-custom", map, child2);

    session.close();
  }  
  
  public void testStaticIncludeTag() throws Exception{
    RWSession session = (RWSession)reg.open();
    session.begin();
    RWNode node = (RWNode)reg.getRoot();
    RegistryConfigLoader loader = new RegistryConfigLoader(node);
    loader.load(new File("etc/parentFile.xml"));
    session.commit();

    HashMap map = new HashMap();
    map.put("username", "jsmith");
    map.put("password", "foo123");
    map.put("url", "jdbc://saturn:5432/");
    map.put("minConnection", "5");    
    map.put("maxConnection", "10");    
    
    Node child1 = node.getChild(Path.parse("databases/000"));
    assertNode("000", map, child1);
    
  }
  
  void assertInclude(String name, Collection children){
    Iterator itr = children.iterator();
    while(itr.hasNext()){
      Node child = (Node)itr.next();
      if(child.getName().equals(name)){
        return;
      }
    }
    fail("Could not find child for: " + name);
  }
  
  protected static void assertNode(String eNodeName, Map eProperties, Node actual) {
    assertNotNull("The actula node to assert should not be null", actual);
    assertEquals("The node name is invalid", eNodeName, actual.getName());
    assertEquals("The number of properties of the node is invalid", eProperties.size(), actual.getProperties().size());
    for (Iterator it = eProperties.keySet().iterator(); it.hasNext(); ) {
      String eKey = (String) it.next();
      Object eValue = eProperties.get(eKey);
      Property actualProp = actual.getProperty(eKey);
      assertNotNull("No value found for the key '" + eKey + "' in the actual node", actualProp);
      assertEquals("The value of the property '" + eKey + "' is invalid", eValue, actualProp.asString());
    }
  }

}
