package org.sapia.resource;

import java.io.File;

import junit.framework.TestCase;

public class FileResourceHandlerTest extends TestCase {

  public FileResourceHandlerTest(String arg0) {
    super(arg0);
  }

  public void testAccepts(){
    FileResourceHandler handler = new FileResourceHandler();
    assertTrue(handler.accepts("test/file.xml"));
    assertTrue(handler.accepts("file:/test/file.xml"));    
    assertTrue(handler.accepts("file:test/file.xml"));    
    assertTrue(!handler.accepts("resource:test/file.xml"));    
  }
  
  public void testGetResource() throws Exception{
    FileResourceHandler handler = new FileResourceHandler();
    handler.getResourceObject("etc/testFileResource.txt");
    handler.getResourceObject("file:etc/testFileResource.txt");    
    handler.getResourceObject("file:" + new File(".").getCanonicalPath() + "/etc/testFileResource.txt");    
  }
  
  public void testMangleUri() throws Exception{
  	FileResourceHandler handler = new FileResourceHandler();
  	String uri = "file:etc/testFileResource.txt";
  	String expectedUri = Utils.replaceWinFileSep(System.getProperty("user.dir")) + File.separator + "etc/testFileResource.txt";
  }
}
