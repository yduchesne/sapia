package org.sapia.resource;

import junit.framework.TestCase;

public class ClasspathResourceHandlerTest extends TestCase {

  public ClasspathResourceHandlerTest(String arg0) {
    super(arg0);
  }

  public void testAccepts(){
    ClasspathResourceHandler handler = new ClasspathResourceHandler();
    assertTrue(handler.accepts("test/file.xml"));
    assertTrue(handler.accepts("resource:/test/file.xml"));    
    assertTrue(handler.accepts("resource:test/file.xml"));    
    assertTrue(!handler.accepts("file:test/file.xml"));    
  }
  
  public void testGetResource() throws Exception{
    ClasspathResourceHandler handler = new ClasspathResourceHandler();
    handler.getResourceObject("org/sapia/resource/testUrlResource.txt");
  }
  
}
