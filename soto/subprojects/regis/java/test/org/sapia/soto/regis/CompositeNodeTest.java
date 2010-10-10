package org.sapia.soto.regis;

import java.util.Collection;
import java.util.Map;

import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.Query;
import org.sapia.regis.impl.NodeImpl;

import junit.framework.TestCase;

public class CompositeNodeTest extends TestCase {

  CompositeNode comp;
  
  protected void setUp() throws Exception {
    comp = new CompositeNode();
    NodeImpl child1 = new NodeImpl(null, "child1", false);
    child1.setProperty("prop1", "value1");
    child1.setProperty("overridden", "base");
    comp.addNode(null, child1);
    NodeImpl child2 = new NodeImpl(null, "test", false);    
    child2.setProperty("prop2", "value2");
    child2.setProperty("overridden", "ext");
    comp.addNode("child2", child2);
    ((NodeImpl)child2.createChild("child3")).setProperty("prop3", "value3");
  }

  public void testGetChild(){
    Node n = comp.getChild("child1");
    super.assertEquals("child1", n.getName());
    n = comp.getChild("child2");
    super.assertEquals("test", n.getName());    
  } 
  
  public void testGetChildWithPath(){
    Node n = comp.getChild(Path.parse("child2/child3"));
    super.assertEquals("child3", n.getName());
  }   

  public void testGetProperty(){
    super.assertEquals("base", comp.getChild("child1").getProperty("overridden").asString());
    super.assertEquals("ext", comp.getProperty("overridden").asString());
    super.assertTrue(comp.getProperty("null").isNull());
  }   
  
  public void testGetChildren(){
    super.assertEquals(2, comp.getChildren().size());
    super.assertTrue(comp.getChildren().iterator().next() instanceof Node);
  }
  
  public void testGetNodes(){
    Collection result = comp.getNodes(Query.create().addCrit("prop1", "value1"));
    super.assertEquals(1, result.size());
    super.assertEquals("child1", ((Node)result.iterator().next()).getName());
    
    result = comp.getNodes(Query.create().addCrit("prop3", "value3").setPath("child2"));
    super.assertEquals(1, result.size());
    super.assertEquals("child3", ((Node)result.iterator().next()).getName());
  } 
  
  public void testGetProperties(){
    Map props = comp.getProperties();
    super.assertEquals("value1", props.get("prop1"));
    super.assertEquals("value2", props.get("prop2"));    
    super.assertEquals("ext", props.get("overridden"));    
  }
  
  public void testGetPropertyKeys(){
    Collection keys = comp.getPropertyKeys(); 
    super.assertTrue(keys.contains("prop1"));
    super.assertTrue(keys.contains("prop2"));
  }

}
