package org.sapia.archie.impl;

import java.util.ArrayList;
import java.util.List;
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
public class SelectionHelperTest extends TestCase{
  
  /**
   * @param arg0
   */
  public SelectionHelperTest(String arg0) {
    super(arg0);
  }  
 
  public void testSelectLeastRecentlyUsed() throws Exception{
    List offers = new ArrayList();
    Offer o1 = new Offer(new Properties(), "Offer1");
    Offer o2 = new Offer(new Properties(), "Offer2");
    offers.add(o1);
    offers.add(o2);
    Thread.sleep(500);

    Offer selected = SelectionHelper.selectLeastRecentlyUsed(offers);
    super.assertEquals("Offer1", selected.getObject().toString());
    Thread.sleep(500);
    selected = SelectionHelper.selectLeastRecentlyUsed(offers);
    super.assertEquals("Offer2", selected.getObject().toString());    
  }
  
  public void testSelectLeastUsed() throws Exception{
    List offers = new ArrayList();
    Offer o1 = new Offer(new Properties(), "Offer1");
    Offer o2 = new Offer(new Properties(), "Offer2");
    offers.add(o1);
    offers.add(o2);
    Offer selected = SelectionHelper.selectLeastUsed(offers);
    super.assertEquals("Offer1", selected.getObject().toString());
    selected = SelectionHelper.selectLeastUsed(offers);
    super.assertEquals("Offer2", selected.getObject().toString());    
  }  

}
