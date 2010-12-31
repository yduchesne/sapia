package org.sapia.util.unit;

import junit.framework.TestCase;

public class StorageUnitTest extends TestCase {

  public StorageUnitTest(String arg0) {
    super(arg0);
  }
  
  public void testParseByte(){
    assertEquals(1, StorageUnit.PROTOTYPE.parseLong("1b", null));
  }

  public void testParseKB(){
    assertEquals(1, StorageUnit.PROTOTYPE.parseLong("1kb", null));
  }  
  
  public void testParseMB(){
    assertEquals(1, StorageUnit.PROTOTYPE.parseLong("1mb", null));
  }  

  public void testParseGB(){
    assertEquals(1, StorageUnit.PROTOTYPE.parseLong("1gb", null));
  }  
  
  public void convertByte(){
    assertEquals(1000, StorageUnit.PROTOTYPE.parseLong("1kb", StorageUnit.BYTE));
  }
  
  public void convertKB(){
    assertEquals(1000, StorageUnit.PROTOTYPE.parseLong("1mb", StorageUnit.KILOBYTE));
  }  
  
  public void convertMB(){
    assertEquals(1000, StorageUnit.PROTOTYPE.parseLong("1gb", StorageUnit.MEGABYTE));
  }  
  
  public void convertGB(){
    assertEquals(1, StorageUnit.PROTOTYPE.parseLong("1000mb", StorageUnit.GIGABYTE));
  }  
  
}
