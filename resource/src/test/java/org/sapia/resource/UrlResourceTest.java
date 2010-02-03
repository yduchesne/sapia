package org.sapia.resource;

import junit.framework.TestCase;

public class UrlResourceTest extends TestCase {

  public UrlResourceTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }
  
  public void testGetInputStream() throws Exception{
    UrlResource res = new UrlResource(Thread.currentThread().getContextClassLoader().getResource("org/sapia/resource/testUrlResource.txt"));
    res.getInputStream().close();
  }
  
  public void testGetRelative() throws Exception{
    UrlResource parent = new UrlResource(Thread.currentThread().getContextClassLoader().getResource("org/sapia/resource/testUrlResource.txt"));
    Resource child = parent.getRelative("testRelativeUrlResource.txt");
    child.getInputStream().close();    
  }  

}
