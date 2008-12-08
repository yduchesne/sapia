package org.sapia.regis.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.sapia.regis.DuplicateNodeException;
import org.sapia.regis.Path;
import org.sapia.regis.Node;
import org.sapia.regis.Query;
import org.sapia.regis.RWNode;
import org.sapia.regis.impl.NodeImpl;

import junit.framework.TestCase;

public class NodeImplTest extends TestCase {
  
  public void testGetAbsolutePath() throws Exception{
    NodeImpl parent = new NodeImpl(null, "");
    RWNode child1 = (RWNode)parent.createChild("child1");
    RWNode child2 = (RWNode)child1.createChild("child2");
    
    Path path1 = child1.getAbsolutePath();
    super.assertEquals(1, path1.tokenCount());
    super.assertEquals("child1", path1.tokens().next());
    
    Path path2 = child2.getAbsolutePath();
    super.assertEquals(2, path2.tokenCount());
    Iterator tokens = path2.tokens();
    super.assertEquals("child1", tokens.next());    
    super.assertEquals("child2", tokens.next());    
    
  }
  public void testRoot() throws Exception{
    NodeImpl root = new NodeImpl(null, "");
    super.assertTrue(root.isRoot());
  }

  public void testCreateGetChild() throws Exception{
    NodeImpl parent = new NodeImpl(null, "");
    Node child = parent.createChild("child");
    super.assertEquals(parent, child.getParent());
    try {
      parent.createChild("child");
      fail("duplicate node created");
    } catch (DuplicateNodeException e) {
      //ok
    }
    child = parent.getChild("child");
    super.assertEquals("child", child.getName());
    
  }

  public void testDeleteChild() throws Exception{
    NodeImpl parent = new NodeImpl(null, "");
    parent.createChild("child");
    parent.deleteChild("child");
    super.assertTrue(parent.getChild("child") == null);
  }

  public void testGetChildren() throws Exception{
    NodeImpl parent = new NodeImpl(null, "");
    parent.createChild("child1");
    parent.createChild("child2");
    super.assertEquals(2, parent.getChildren().size());
  }
  
  public void testInclude() throws Exception{
    NodeImpl parent = new NodeImpl(null, "");
    Node child1 = parent.createChild("child1");
    Node child2 = parent.createChild("child2");
    RWNode child3 = (RWNode)parent.createChild("child3");
    child3.addInclude(child1);
    child3.addInclude(child2);
    super.assertEquals(2, child3.getChildren().size());
    Collection children = parent.getChildren();
    super.assertTrue(children.contains(child1));    
    super.assertTrue(children.contains(child2));
    
    assertEquals(child1, child3.getChild("child1"));
    assertEquals(child2, child3.getChild("child2"));    
  }  
  
  public void testInheritance() throws Exception{
    NodeImpl parent = new NodeImpl(null, "");
    parent.setProperty("prop1", "value1");
    
    RWNode child = (RWNode)parent.createChild("child1");
    child.setProperty("prop2", "value2");
    super.assertTrue(child.getProperty("prop1").isNull());
    child.setInheritsParent(true);
    super.assertEquals("value1", child.getProperty("prop1").getValue());    
  }
  
  public void testLink() throws Exception{
    NodeImpl parent = new NodeImpl(null, "");
    
    parent.setProperty("var", "${prop1},${prop2}");
    parent.setProperty("prop1", "value3");
    RWNode link1 = new NodeImpl(null, "link1");
    link1.setProperty("prop1", "value1");
    RWNode link2 = new NodeImpl(null, "link1");
    link2.setProperty("prop2", "value2");
    
    parent.prependLink(link1);
    parent.appendLink(link2);
    super.assertEquals("value1", parent.getProperty("prop1").getValue());    
    super.assertEquals("value2", parent.getProperty("prop2").getValue());    
    super.assertEquals("value1,value2", parent.renderProperty("var").getValue());
  }  
  
  public void testInheritedLastModifChecksum() throws Exception{
    NodeImpl parent = new NodeImpl(null, "");
    NodeImpl child1 = (NodeImpl)parent.createChild("child1");
    child1.setInheritsParent(true);
    NodeImpl child2 = (NodeImpl)child1.createChild("child2");
    child2.setInheritsParent(true);
    
    long chk = parent.lastModifChecksum();
    parent.setVersion(parent.getVersion()+1);
    super.assertTrue(chk != parent.lastModifChecksum());
    
    chk = child1.lastModifChecksum();
    super.assertEquals(chk, child1.lastModifChecksum());
    child1.setVersion(child1.getVersion()+1);
    super.assertTrue(chk != child1.lastModifChecksum());
    chk = child1.lastModifChecksum();
    super.assertEquals(chk, child1.lastModifChecksum());    
    parent.setVersion(parent.getVersion()+1);    
    super.assertTrue(chk != child1.lastModifChecksum());    
    
    chk = child2.lastModifChecksum();
    super.assertEquals(chk, child2.lastModifChecksum());
    child2.setVersion(child2.getVersion()+1);
    super.assertTrue(chk != child2.lastModifChecksum());
    chk = child2.lastModifChecksum();
    super.assertEquals(chk, child2.lastModifChecksum());    
    child1.setVersion(child1.getVersion()+1);    
    super.assertTrue(chk != child2.lastModifChecksum());    
    chk = child2.lastModifChecksum();
    super.assertEquals(chk, child2.lastModifChecksum());    
    parent.setVersion(parent.getVersion()+1);    
    super.assertTrue(chk != child2.lastModifChecksum());    
  }
  
  public void testLinkedLastModifChecksum() throws Exception{
    NodeImpl parent = new NodeImpl(null, "");
    NodeImpl link1  = new NodeImpl(null, "");
    NodeImpl link2  = new NodeImpl(null, "");
    parent.prependLink(link1);
    parent.appendLink(link2);
    
    long chk = parent.lastModifChecksum();
    link1.setVersion(link1.getVersion()+1);
    super.assertTrue(chk != parent.lastModifChecksum());
    
    chk = parent.lastModifChecksum();    
    link2.setVersion(link2.getVersion()+1);
    super.assertTrue(chk != parent.lastModifChecksum());    
  }
  
  public void testGetProperties(){
    NodeImpl parent = new NodeImpl(null, "");
    NodeImpl child1 = new NodeImpl(parent, "child1");    
    NodeImpl child2 = new NodeImpl(child1, "child2");
    NodeImpl link1  = new NodeImpl(null, "");
    NodeImpl link2  = new NodeImpl(null, "");

    child1.setInheritsParent(true);
    child2.setInheritsParent(true);
    child2.prependLink(link1);
    child2.appendLink(link2);    

    parent.setProperty("parent", "parentProp");
    child1.setProperty("child1", "${parent}");    
    child1.setProperty("override", "child1");    
    link1.setProperty("link1", "link1Prop");
    link2.setProperty("link2", "${append}");    
    child2.setProperty("child2", "${link1} ${child1}");
    child2.setProperty("override", "child2");
    child2.setProperty("append", "appendProp");
    child2.setProperty("this", "${child2}");
    
    Map props =  child2.getProperties();
    assertEquals("link1Prop parentProp", props.get("child2"));
    assertEquals("child2", props.get("override"));
    assertEquals("link1Prop parentProp", props.get("this"));
    assertEquals("appendProp", props.get("link2"));
    
  }
  
  public void testGetNodesForQuery() throws Exception{
    NodeImpl parent = new NodeImpl(null, "");
    NodeImpl child1 = (NodeImpl)parent.createChild("child1");
    NodeImpl subChild = (NodeImpl)child1.createChild("child1_1");
    subChild.setProperty("prop1", "value1");
    subChild = (NodeImpl)child1.createChild("child1_2");
    subChild.setProperty("prop1", "value1");    
    subChild.setProperty("prop2", "value2");    
    subChild = (NodeImpl)child1.createChild("child1_3");
    subChild.setProperty("prop1", "value1");    
    subChild.setProperty("prop2", "value2");
    subChild.setProperty("prop3", "value3");    

    Collection nodes = parent.getNodes(Query.create(""));
    Node node = (Node)nodes.iterator().next();
    assertEquals("child1", node.getName());
    assertEquals(1, nodes.size());
    
    nodes = parent.getNodes(Query.create("child1"));
    assertEquals(3, nodes.size());    
    
    nodes = parent.getNodes(Query.create("child1").addCrit("prop1", "value1"));
    assertEquals(3, nodes.size());    
    
    nodes = parent.getNodes(Query.create("child1")
        .addCrit("prop1", "value1").addCrit("prop2", "value2"));
    assertEquals(2, nodes.size());    
    
    nodes = parent.getNodes(Query.create("child1")
        .addCrit("prop3", "value3"));
    assertEquals(1, nodes.size());     
  }

}
