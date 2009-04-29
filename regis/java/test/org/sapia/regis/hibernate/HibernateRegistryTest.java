package org.sapia.regis.hibernate;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.TestCase;

import org.hibernate.SessionFactory;
import org.sapia.regis.Path;
import org.sapia.regis.Node;
import org.sapia.regis.RWNode;
import org.sapia.regis.RWSession;
import org.sapia.regis.RegisSession;
import org.sapia.regis.loader.RegistryConfigLoader;

public class HibernateRegistryTest extends TestCase {

  private HibernateRegistry regis;
  
  protected void setUp() throws Exception {
    //BasicConfigurator.configure();
    regis = openRegis();
  }
  
  private HibernateRegistry openRegis() throws Exception{
    Properties props = new Properties();
    InputStream is = null;
    props.load(is = new FileInputStream(new File("etc/hibernate.properties")));
    is.close();
    return (HibernateRegistry)new HibernateRegistryFactory().connect(props);
  }
  
  private HibernateRegistry openRegis(SessionFactory fac) throws Exception{
    return new HibernateRegistry(fac, true);
  }  

  public void testGetRoot() {
    RegisSession s = regis.open();
    Node root = regis.getRoot();
    super.assertEquals(true, root.isRoot());
    s.close();
    regis.close();
  }

  public void testCreateNode() throws Exception{
    RWSession s;
    s = (RWSession)regis.open();
    s.begin();
    Node node = regis.createNode(Path.parse("child1/child2"));
    s.commit();
    s.close();
    
    regis = openRegis(regis.getSessionFactory());
    
    s = (RWSession)regis.open();
    s.begin();
    node = regis.getRoot().getChild(Path.parse("child1/child2"));
    super.assertTrue(node != null);
    s.commit();
    s.close();
    regis.close();    
  }
  
  public void testConfiguration() throws Exception{
    RWSession s;
    s = (RWSession)regis.open();
    s.begin();
    RWNode node = (RWNode)regis.createNode(Path.parse("child1/child2"));
    node.setProperty("string", "stringValue");
    node.setProperty("int", "100");
    s.commit();
    s.close();
    
    regis = openRegis(regis.getSessionFactory());
    s = (RWSession)regis.open();
    s.begin();
    node = (RWNode)regis.getRoot().getChild(Path.parse("child1/child2"));
    super.assertEquals("stringValue", node.getProperty("string").asString());
    super.assertEquals(100, node.getProperty("int").asInt());    
    s.commit();
    s.close();
    regis.close();    
  }
  
  public void testXmlConfiguration() throws Exception{
    RWSession s = (RWSession)regis.open();
    s.begin();
    RWNode node = (RWNode)regis.getRoot();
    RegistryConfigLoader loader = new RegistryConfigLoader(node);
    loader.load(new File("etc/configCreateExample.xml"));
    s.commit();
    
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
    
    s.close();
    
  }  

}
