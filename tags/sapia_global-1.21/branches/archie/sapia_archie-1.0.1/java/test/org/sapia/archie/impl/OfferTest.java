package org.sapia.archie.impl;

import java.util.Properties;

import junit.framework.TestCase;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class OfferTest extends TestCase{
  
  /**
   * @param arg0
   */
  public OfferTest(String arg0) {
    super(arg0);
  }
  
  public void testMatches(){
    Properties attributes = new Properties();
    attributes.put("attr1", "val1");
    attributes.put("attr2", "val2");    
    attributes.put("attr3", "val3");    
    Offer offer = new Offer(attributes, "SomeObject");
    
    Properties constraints = new Properties();
    constraints.setProperty("attr1", "val1");
    super.assertTrue(offer.matches(constraints));
    constraints.setProperty("attr2", "val2");    
    super.assertTrue(offer.matches(constraints));    
    constraints.setProperty("attr3", "val3");    
    super.assertTrue(offer.matches(constraints));
    constraints.setProperty("attr4", "val4");    
    super.assertTrue(!offer.matches(constraints));    
  }
}

