package org.sapia.resource;

import junit.framework.TestCase;

public class UrlResourceHandlerTest extends TestCase {

  public UrlResourceHandlerTest(String arg0) {
    super(arg0);
  }
  
  public void testAccepts(){
    UrlResourceHandler handler = new UrlResourceHandler();    
    assertTrue(handler.accepts("file:test.txt"));
    assertTrue(!handler.accepts("test.txt"));    
  }

  public void testResolveResource() throws Exception{
    UrlResourceHandler handler = new UrlResourceHandler();
    handler.getResource("file:etc/testFileResource.txt");
  }
}
