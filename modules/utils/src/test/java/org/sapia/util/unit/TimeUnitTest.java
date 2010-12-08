package org.sapia.util.unit;

import junit.framework.TestCase;

public class TimeUnitTest extends TestCase {

  public TimeUnitTest(String arg0) {
    super(arg0);
  }

  public void testParseMillis(){
    assertEquals(1, TimeUnit.PROTOTYPE.parseLong("1ms", null));
  }
  
  public void testParseSecond(){
    assertEquals(1, TimeUnit.PROTOTYPE.parseLong("1s", null));
  }
  
  public void testParseMinute(){
    assertEquals(1, TimeUnit.PROTOTYPE.parseLong("1m", null));
  }  
  
  public void testParseHour(){
    assertEquals(1, TimeUnit.PROTOTYPE.parseLong("1h", null));
  }  
  
  public void testParseDay(){
    assertEquals(1, TimeUnit.PROTOTYPE.parseLong("1D", null));
  }  
  
  public void testParseMonth(){
    assertEquals(1, TimeUnit.PROTOTYPE.parseLong("1M", null));
  }    
  
  public void testParseYear(){
    assertEquals(1, TimeUnit.PROTOTYPE.parseLong("1Y", null));
  }  
  
  public void convertMS(){
    assertEquals(1000, TimeUnit.PROTOTYPE.parseLong("1s", TimeUnit.MILLIS));
  }  
  
  public void convertSecond(){
    assertEquals(60, TimeUnit.PROTOTYPE.parseLong("1m", TimeUnit.SECOND));
  }  
  
  public void convertMinute(){
    assertEquals(60, TimeUnit.PROTOTYPE.parseLong("1h", TimeUnit.MINUTE));
  }  
  
  public void convertHour(){
    assertEquals(24, TimeUnit.PROTOTYPE.parseLong("1D", TimeUnit.HOUR));
  }  
  
  public void convertDay(){
    assertEquals(30, TimeUnit.PROTOTYPE.parseLong("1M", TimeUnit.DAY));
  }  
  
  public void convertMonth(){
    assertEquals(12, TimeUnit.PROTOTYPE.parseLong("1Y", TimeUnit.MONTH));
  }
  
  public void convertYear(){
    assertEquals(1, TimeUnit.PROTOTYPE.parseLong("365", TimeUnit.YEAR));
  }    
  
}
