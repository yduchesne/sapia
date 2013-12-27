package org.sapia.util.unit;

import junit.framework.TestCase;

public class DistanceUnitTest extends TestCase {

  public DistanceUnitTest(String arg0) {
    super(arg0);
  }

  public void testParseMM(){
    assertEquals(1, DistanceUnit.PROTOTYPE.parseLong("1mm", null));
  }
  
  public void testParseCM(){
    assertEquals(1, DistanceUnit.PROTOTYPE.parseLong("1cm", null));
  }    
  
  public void testParseMeter(){
    assertEquals(1, DistanceUnit.PROTOTYPE.parseLong("1m", null));
  }
  
  public void testParseKM(){
    assertEquals(1, DistanceUnit.PROTOTYPE.parseLong("1km", null));
  }    
  
  public void convertMM(){
    assertEquals(1000, DistanceUnit.PROTOTYPE.parseLong("1m", DistanceUnit.MILLIMETER));
  }
  
  public void convertCM(){
    assertEquals(100, DistanceUnit.PROTOTYPE.parseLong("1m", DistanceUnit.CENTIMETER));
  }
  
  public void convertM(){
    assertEquals(1, DistanceUnit.PROTOTYPE.parseLong("1000m", DistanceUnit.KILOMETER));
  }
  
  public void convertKM(){
    assertEquals(1000, DistanceUnit.PROTOTYPE.parseLong("1km", DistanceUnit.METER));
  }      
    
}
