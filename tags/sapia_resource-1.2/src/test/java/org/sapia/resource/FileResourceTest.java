package org.sapia.resource;

import java.io.File;

import junit.framework.TestCase;

public class FileResourceTest extends TestCase {

  public FileResourceTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testGetInputStream() throws Exception{
    Resource res = new FileResource(new File("etc/testFileResource.txt"));
    res.getInputStream().close();
  }
  
  public void testGetRelative() throws Exception{
    Resource parent = new FileResource(new File("etc/testFileResource.txt"));
    Resource child = parent.getRelative("testRelativeFileResource.txt");
    child.getInputStream().close();    
  }    
}
