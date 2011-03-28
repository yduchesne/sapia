package org.sapia.soto;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class AttributeSelectorTest extends TestCase{
  
  private SotoContainer container;
  private TestService svc1, svc2;
  
  public AttributeSelectorTest(String name){
    super(name);
  }
  
  /**
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception {
    container = new SotoContainer();
    svc1 = new TestService();
    svc2 = new TestService();
    ServiceMetaData data = new ServiceMetaData(container, "test1", svc1, new ArrayList());
    data.addAttribute(new Attribute().setName("attr1").setValue("val1"));
    data.addAttribute(new Attribute().setName("attr2").setValue("val2"));    
    container.bind(data);
    data = new ServiceMetaData(container, "test2", svc2, new ArrayList());
    data.addAttribute(new Attribute().setName("attr2").setValue("val3"));    
    container.bind(data);    
  }
  
  public void testLookupForName() throws Exception {
    List results = container.lookup(
        new AttributeServiceSelector()
          .addCriteria(new Attribute().setName("attr1")),
        false);
    super.assertEquals(1, results.size());
    super.assertTrue(results.get(0) instanceof Service);    
    results = container.lookup(
         new AttributeServiceSelector()
           .addCriteria(new Attribute().setName("attr2")),
         true);
    super.assertTrue(results.get(0) instanceof ServiceMetaData);    
    super.assertEquals(2, results.size());
  }
  
  public void testLookupForValue() throws Exception {
    List results = container.lookup(
         new AttributeServiceSelector()
           .addCriteria(new Attribute().setName("attr2").setValue("val2")),
         false);
    super.assertTrue(results.get(0) == svc1);
    
    results = container.lookup(
        new AttributeServiceSelector()
          .addCriteria(new Attribute().setName("attr2").setValue("val3")),
        false);
    super.assertTrue(results.get(0) == svc2);    
  }    

}
