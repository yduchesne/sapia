package org.sapia.resource;

import junit.framework.TestCase;

public class UtilsTest extends TestCase {
  
  private String winUri = "c:\\some\\path";
  private String linAbsUri = "/some/path";  
  private String linUri = "some/path";  

  public UtilsTest(String name){ super(name); }

  public void testHasScheme() {
    assertTrue(!Utils.hasScheme(winUri));
    assertTrue(!Utils.hasScheme(linAbsUri));    
    assertTrue(!Utils.hasScheme(linUri));    
    assertTrue(Utils.hasScheme("file:" + winUri));    
  }

  public void testChopScheme() {
    assertEquals(winUri, Utils.chopScheme(winUri));
    assertEquals(linUri, Utils.chopScheme(linUri));    
    assertEquals(linAbsUri, Utils.chopScheme(linAbsUri));    
  }

  public void testGetScheme() {
    assertTrue(Utils.getScheme(winUri) == null);
    assertTrue(Utils.getScheme(linAbsUri) == null);    
    assertTrue(Utils.getScheme(linUri) == null);   
    assertEquals("file", Utils.getScheme("file:" + linAbsUri));    
    assertEquals("file", Utils.getScheme("file:" + winUri));    
  }
  
  public void testIsAbsolute() throws Exception{
    assertTrue(Utils.isAbsolute(winUri.replace("\\", "/")));
    assertTrue(!Utils.isAbsolute(linUri));
    assertTrue(Utils.isAbsolute("file:" + linAbsUri));    
  }

}
