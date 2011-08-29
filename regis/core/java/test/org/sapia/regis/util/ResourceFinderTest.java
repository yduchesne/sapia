package org.sapia.regis.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.TestCase;

public class ResourceFinderTest extends TestCase {

  public ResourceFinderTest(String arg0) {
    super(arg0);
  }

  public void testFindCatchAllResource() throws IOException{
    InputStream is = ResourceFinder.findResource("(dummy.properties), etc/bootstrap2.properties");
    Properties props = new Properties();
    props.load(is);
    is.close();
    assertEquals("file:etc/bootstrap2.xml", props.getProperty("org.sapia.regis.local.bootstrap"));
  }
  
  public void testFindResource() throws IOException{
    InputStream is = ResourceFinder.findResource("(etc/bootstrap1.properties), etc/bootstrap2.properties");
    Properties props = new Properties();
    props.load(is);
    is.close();
    assertEquals("file:etc/bootstrap1.xml", props.getProperty("org.sapia.regis.local.bootstrap"));
  }  
  
  public void testFindMisconfiguredResource() throws IOException{
    InputStream is = ResourceFinder.findResource("(etc/bootstrap1.properties, etc/bootstrap2.properties");
    Properties props = new Properties();
    props.load(is);
    is.close();
    assertEquals("file:etc/bootstrap1.xml", props.getProperty("org.sapia.regis.local.bootstrap"));
  }
  
  public void testGetResource() throws IOException{
    InputStream is = ResourceFinder.getResource("(etc/foo.properties), etc/bar.properties");
    assertTrue(is == null);
  }

  public void testFindProperties() throws IOException{
    Properties props = new Properties();      
    ResourceFinder.findProperties("(etc/foo.properties), etc/bootstrap2.properties", props);
    assertEquals("file:etc/bootstrap2.xml", props.getProperty("org.sapia.regis.local.bootstrap"));

    props.clear();
    ResourceFinder.findProperties("(etc/foo.properties, etc/bootstrap2.properties), etc/bootstrap1.properties", props);
    assertEquals("file:etc/bootstrap1.xml", props.getProperty("org.sapia.regis.local.bootstrap"));    
    
    props.clear();
    try{
      ResourceFinder.findProperties("(etc/foo.properties, etc/bar.properties), etc/snafu.properties", props);
      fail("Should have thrown FileNotFoundException");
    }catch(FileNotFoundException e){}
  }
  
  public void testLoadProperties() throws IOException{
    Properties props = new Properties();      
    ResourceFinder.loadProperties("(etc/foo.properties, etc/bar.properties), etc/snafu.properties", props);
  }
  
  public void testLoadReplaceProperties() throws IOException{
    Properties props = new Properties();      
    System.setProperty("rootDir", ".");
    ResourceFinder.loadProperties("(etc/first.properties, etc/bar.properties), etc/second.properties", props);
    
    assertEquals("./etc", props.getProperty("baseDir"));
    assertEquals("./etc/bootstrap1.properties", props.getProperty("path"));    
    assertEquals("./etc/bootstrap1.properties.bak", props.getProperty("backup"));
  }
  
  public void testLoadResource() throws Exception{
    ResourceFinder.getResource("/org/sapia/regis/util/testResource.txt").close();
  }
  
}
