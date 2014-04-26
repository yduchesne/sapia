package org.sapia.archie.impl;

import java.util.ArrayList;
import java.util.List;

import org.sapia.archie.NamePart;
import org.sapia.archie.Node;

import junit.framework.TestCase;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class AttributeNodeTest extends TestCase{
  
  Node _node;
  
  public AttributeNodeTest(String name){
    super(name);
  }
  
  /**
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception {
    _node = new AttributeNodeFactory().newNode();
  }
  
  public void testPutValue() throws Exception{
    NamePart n1 = _node.getNameParser().parseNamePart("object?attr1=val1&attr2=val2");
    NamePart n2 = _node.getNameParser().parseNamePart("object?attr1=val1");
    
    super.assertTrue(_node.putValue(n1, "object1", true));
    super.assertTrue(_node.putValue(n2, "object2", true));
  }
  
  public void testGetValue() throws Exception{
    NamePart n1 = _node.getNameParser().parseNamePart("object?attr1=val1&attr2=val2");
    NamePart n2 = _node.getNameParser().parseNamePart("object?attr1=val1");
    
    _node.putValue(n1, "object1", true);
    _node.putValue(n2, "object2", true);
    
    super.assertEquals("object1", _node.getValue(n2));
    super.assertEquals("object1", _node.getValue(n1));    
    super.assertEquals("object2", _node.getValue(n2));    
  }
  
  public void testRemoveValue() throws Exception{
    NamePart n1 = _node.getNameParser().parseNamePart("object?attr1=val1&attr2=val2");
    NamePart n2 = _node.getNameParser().parseNamePart("object?attr1=val1");
    
    _node.putValue(n1, "object1", true);
    _node.putValue(n2, "object2", true);
    
    super.assertTrue(_node.getValue(n2) != null);
    super.assertTrue(_node.getValue(n1) != null);    
    super.assertTrue(_node.getValue(n2) != null);
    
    _node.removeValue(n2);
    super.assertTrue(_node.getValue(n2) == null);
    
    _node.putValue(n1, "object1", true);
    _node.putValue(n2, "object2", true);
    
    super.assertTrue(_node.getValue(n2) != null);
    super.assertTrue(_node.getValue(n1) != null);    
    super.assertTrue(_node.getValue(n2) != null);
    
    _node.removeValue(n1);    
    
    super.assertTrue(_node.getValue(n1) == null);    
    super.assertTrue(_node.getValue(n2) != null);    
    
    

  }  
  
  public void testSelectMatchingOffers() throws Exception{
    AttributeNamePart n1 = (AttributeNamePart)_node.getNameParser().parseNamePart("object?attr1=val1&attr2=val2");
    AttributeNamePart n2 = (AttributeNamePart)_node.getNameParser().parseNamePart("object?attr1=val1");
    List offers = new ArrayList();
    Offer o1 = new Offer(n1.getAttributes(), "o1");
    Offer o2 = new Offer(n1.getAttributes(), "o2");
    offers.add(o1);
    offers.add(o2);
    super.assertEquals("o1", ((Offer)AttributeNode.selectMatchingOffers(n1, offers).get(0)).getObject());
    super.assertEquals("o2", ((Offer)AttributeNode.selectMatchingOffers(n2, offers).get(1)).getObject());    
    
  }
}
