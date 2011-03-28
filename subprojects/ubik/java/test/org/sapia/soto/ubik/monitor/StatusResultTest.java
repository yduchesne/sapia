package org.sapia.soto.ubik.monitor;

import junit.framework.TestCase;

public class StatusResultTest extends TestCase {

  public StatusResultTest(String arg0) {
    super(arg0);
  }
  
  public void testCompareId(){
    StatusResult r1 = new StatusResult("r1", "r1.class");
    StatusResult r2 = new StatusResult("r2", "r2.class");
    super.assertTrue(r1.compareTo(r2) < 0);
    super.assertTrue(r2.compareTo(r1) > 0);    
  }  
  
  public void testCompareNullId(){
    StatusResult r1 = new StatusResult("r1", "r1.class");
    StatusResult r2 = new StatusResult(null, "r2.class");
    super.assertTrue(r1.compareTo(r2) < 0);
    super.assertTrue(r2.compareTo(r1) > 0);    
  }
  
  public void testCompareClassName(){
    StatusResult r1 = new StatusResult(null, "r1.class");
    StatusResult r2 = new StatusResult(null, "r2.class");
    super.assertTrue(r1.compareTo(r2) < 0);
    super.assertTrue(r2.compareTo(r1) > 0);    
  }  

}
