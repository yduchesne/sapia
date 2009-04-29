package org.sapia.regis.cache;

import org.sapia.regis.Property;
import org.sapia.regis.Node;
import org.sapia.regis.RWNode;
import org.sapia.regis.impl.NodeImpl;
import org.sapia.regis.local.LocalRegistry;

import junit.framework.TestCase;

public class CacheNodeTest extends TestCase {
  
  LocalRegistry wrapped;
  CacheRegistry cacheReg;
  
  public CacheNodeTest(String arg0) {
    super(arg0);
  }
  
  protected void setUp() throws Exception {
    wrapped = new LocalRegistry(true);
    RWNode child1 = (RWNode)((RWNode)wrapped.getRoot()).createChild("child1");
    child1.setProperty("prop1", "val1");
    child1.setProperty("template", "${user.dir}");
    RWNode link = new NodeImpl(null, "link", true);
    child1.appendLink(link);
    child1.prependLink(link);
    RWNode child2 = (RWNode)((RWNode)wrapped.getRoot().getChild("child1")).createChild("child2");
    child2.setProperty("prop2", "100");
    child2.setInheritsParent(true);
    cacheReg = new CacheRegistry(wrapped, 500, true);    
  }
  
  public void testGetNodes() {
    Node child1 = cacheReg.getRoot().getChild("child1");
    super.assertTrue(child1 instanceof CacheNode);
    
    Node child2 = child1.getChild("child2");    
    super.assertTrue(child2 instanceof CacheNode);
    
    Node parent = child2.getParent();    
    super.assertTrue(parent instanceof CacheNode);    
    
  }
  
  public void testChildren(){
    Node child1 = cacheReg.getRoot().getChild("child1");
    super.assertTrue(child1.getChildren().iterator().next() instanceof CacheNode);
  }  
  
  public void testGetLinks(){
    Node child1 = cacheReg.getRoot().getChild("child1");
    super.assertTrue(child1.getLinks(true).iterator().next() instanceof CacheNode);
    super.assertTrue(child1.getLinks(false).iterator().next() instanceof CacheNode);    
  }
  
  public void testGetProperty(){
    Node child1 = cacheReg.getRoot().getChild("child1");
    super.assertEquals("val1", child1.getProperty("prop1").asString());
    
    super.assertEquals(System.getProperty("user.dir"), child1.getProperty("template").asString());
    
    Node child2 = child1.getChild("child2");    
    super.assertEquals("val1", child2.getProperty("prop1").asString());
    super.assertEquals(100, child2.getProperty("prop2").asInt());
  }
  
  public void testRenderProperty(){
    Node child1 = cacheReg.getRoot().getChild("child1");
    super.assertEquals(System.getProperty("user.dir"), child1.getProperty("template").asString());
  }
  
  public void testRefresh() throws InterruptedException{
    NodeImpl child1 =(NodeImpl)wrapped.getRoot().getChild("child1");
    Node child2 = cacheReg.getRoot().getChild("child1").getChild("child2");
    Property prop = child2.getProperty("prop1");
    super.assertEquals("val1", prop.asString());    
    Thread.sleep(1000);    
    child1.setProperty("prop1", "val2");
    child1.setVersion(child1.getVersion()+1);
    super.assertEquals("val2", prop.asString());
  }  

}
