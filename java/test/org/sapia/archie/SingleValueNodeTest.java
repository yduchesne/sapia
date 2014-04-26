package org.sapia.archie;

import org.sapia.archie.impl.*;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class SingleValueNodeTest extends TestCase {
  public SingleValueNodeTest(String name) {
    super(name);
  }
  
  public void testPutGetValue() throws Exception {
    NamePart        name = new DefaultNameParser().parse("name").first();
    SingleValueNode node = new SingleValueNode(new HashMap(), new HashMap(),
                                               new DefaultNodeFactory());
    super.assertTrue(node.putValue(name, "value", true));
    super.assertEquals("value", node.getValue(name));

    super.assertTrue(!node.putValue(name, "value2", false));
    super.assertEquals("value", node.getValue(name).toString());

    super.assertTrue(node.putValue(name, "value2", true));
    super.assertEquals("value2", node.getValue(name).toString());
  }
  
  public void testOnWriteRead() throws Exception{
    NamePart name = new DefaultNameParser().parse("name").first();
    TestSingeValueNode node = new TestSingeValueNode();
    node.putValue(name, "SomeObject", true);
    super.assertTrue(node.write);
    super.assertEquals("SomeObject", node.getValue(name));
    super.assertTrue(node.read);
    
  }
  
  public void testRemoveValue() throws Exception{
    NamePart            name = new DefaultNameParser().parse("name").first();
    SingleValueNode     node = new SingleValueNode(new HashMap(), new HashMap(), new DefaultNodeFactory());
    node.putValue(name, "value", true);
    node.removeValue(name);    
    super.assertTrue(node.getValue(name) == null);  
  }  
  
  public void testCreateChild() throws Exception{
    SingleValueNode     node = new SingleValueNode(new HashMap(), new HashMap(), new DefaultNodeFactory());
    DefaultNamePart n;  
    node.createChild(n = new DefaultNamePart("child"));
    super.assertTrue(node.getChild(n) != null);
  }
  
  public void testRemoveChild() throws Exception{
    SingleValueNode     node = new SingleValueNode(new HashMap(), new HashMap(), new DefaultNodeFactory());
    DefaultNamePart n;  
    node.createChild(n = new DefaultNamePart("child"));
    node.removeChild(n);
    super.assertTrue(node.getChild(n) == null);
  }
  
  public void testGetChildrenCount() throws Exception{
    SingleValueNode     node = new SingleValueNode(new HashMap(), new HashMap(), new DefaultNodeFactory());
    node.createChild(new DefaultNamePart("child1"));    
    node.createChild(new DefaultNamePart("child2")); 
    super.assertEquals(2, node.getChildrenCount());
       
  }  
  
  public void testAccept() throws Exception{
    SingleValueNode     node = new SingleValueNode(new HashMap(), new HashMap(), new DefaultNodeFactory());
    Node child1 = node.createChild(new DefaultNamePart("child1"));    
    Node child2 = node.createChild(new DefaultNamePart("child2")); 
    child1.createChild(new DefaultNamePart("child11"));
    child2.createChild(new DefaultNamePart("child21"));
    
    final List<Node> children = new ArrayList<>();
    
    node.accept(new NodeVisitor() {
      
      @Override
      public boolean visit(Node node) {
        if (node.getParent() != null) {
          children.add(node);
        }
        return true;
      }
    });
    
    assertEquals(4, children.size());
  }  
  
  public void testAcceptAborted() throws Exception{
    SingleValueNode     node = new SingleValueNode(new HashMap(), new HashMap(), new DefaultNodeFactory());
    Node child1 = node.createChild(new DefaultNamePart("child1"));    
    Node child2 = node.createChild(new DefaultNamePart("child2")); 
    child1.createChild(new DefaultNamePart("child11"));
    child2.createChild(new DefaultNamePart("child21"));
    
    final List<Node> children = new ArrayList<>();
    
    node.accept(new NodeVisitor() {
      
      @Override
      public boolean visit(Node node) {
        if (node.getParent() != null) {
          children.add(node);
        }
        return false;
      }
    });
    
    assertEquals(0, children.size());
  }  
  
  static class TestSingeValueNode extends SingleValueNode{
    
    boolean read, write;
    
    TestSingeValueNode() throws ProcessingException{
      super(new HashMap(), new HashMap(), new DefaultNodeFactory());
    }
    
    protected Object onRead(NamePart part, Object toRead) {
      read = true;
      return ((TestBinding)toRead).bound;
    }
    
    /**
     * @see org.sapia.archie.impl.SingleValueNode#onWrite(java.lang.Object)
     */
    protected Object onWrite(NamePart part, Object o) {
      write = true;
      TestBinding bd = new TestBinding();
      bd.bound = o;
      return bd;
    }
  
  }
  
  static class TestBinding{
    Object bound;
  }
}
