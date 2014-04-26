/*
 * Created on 25-Feb-2004
 */
package org.sapia.archie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sapia.archie.impl.DefaultNameParser;
import org.sapia.archie.impl.DefaultNamePart;
import org.sapia.archie.impl.DefaultNodeFactory;
import org.sapia.archie.impl.MultiValueNode;
import org.sapia.archie.impl.SingleValueNode;

import junit.framework.TestCase;

/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class MultiValueNodeTest extends TestCase {
  
  /**
   * @param arg0
   */
  public MultiValueNodeTest(String arg0) {
    super(arg0);
  }
  
  public void testPutGetValue() throws Exception{
    NamePart           name = new DefaultNameParser().parse("name").first();
    TestMultiValueNode node = new TestMultiValueNode();
    
    super.assertTrue(node.putValue(name, "value", true));
    super.assertEquals("value", node.getValue(name));

    super.assertTrue(!node.putValue(name, "value2", false));
    super.assertEquals("value", node.getValue(name).toString());

    super.assertTrue(node.putValue(name, "value2", true));
    
    super.assertEquals("value", node.getValue(name).toString());
    super.assertEquals("value2", node.getValue(name).toString());    
  
  }
  
  public void testRemoveValue() throws Exception{
    NamePart           name = new DefaultNameParser().parse("name").first();
    TestMultiValueNode node = new TestMultiValueNode();
    node.putValue(name, "value", true);
    node.putValue(name, "value2", true);
    node.removeValue(name);    
    super.assertTrue(node.getValue(name) == null);  
  }
  
  public void testOnReadWrite() throws Exception{
    NamePart           name = new DefaultNameParser().parse("name").first();
    TestMultiValueNode node = new TestMultiValueNode();
    node.putValue(name, "value", true);
    super.assertTrue(node.write);
    node.getValue(name);
    super.assertTrue(node.read && node.select);
  }
  
  public void testAccept() throws Exception{
    TestMultiValueNode  node = new TestMultiValueNode();
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
    TestMultiValueNode  node = new TestMultiValueNode();
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
  
  static class TestMultiValueNode extends MultiValueNode{
    
    boolean read, write, select;
    
    TestMultiValueNode() throws ProcessingException{
      super(new HashMap(), new HashMap(), new DefaultNodeFactory());
    }
    
    /**
     * @see org.sapia.archie.impl.MultiValueNode#onSelect(java.util.List)
     */
    protected Object onSelect(List values) {
      select = true;
      return super.onSelect(values);
    }
    
    /**
     * @see org.sapia.archie.impl.MultiValueNode#onRead(org.sapia.archie.NamePart, java.lang.Object)
     */
    protected Object onRead(NamePart np, Object selected) {
      read = true;
      return ((TestBinding)selected).bound;
    }
    
    /**
     * @see org.sapia.archie.impl.MultiValueNode#onWrite(org.sapia.archie.NamePart, java.lang.Object)
     */
    protected Object onWrite(NamePart np, Object toBind) {
      write = true;
      TestBinding bd = new TestBinding();
      bd.bound = toBind;
      return bd;
    }
  }
  
  static class TestBinding{
    Object bound;
  }

}
