package org.sapia.resource.include;

import org.sapia.resource.ClasspathResourceHandler;
import org.sapia.resource.FileResourceHandler;
import org.sapia.resource.ResourceHandlerChain;
import org.sapia.resource.TestIncludeContext;
import org.sapia.resource.TestIncludedObject;

import junit.framework.TestCase;

public class IncludeStateTest extends TestCase {

  public IncludeStateTest(String arg0) {
    super(arg0);
  }

  public void testFileInclude() throws Exception{
    ResourceHandlerChain chain = new ResourceHandlerChain();
    chain.append(new FileResourceHandler());
    chain.append(new ClasspathResourceHandler());
    IncludeConfig config = 
    IncludeState.createConfig(
        "test",
        new IncludeContextFactory(){ 
          public IncludeContext createInstance() {
            return new TestIncludeContext();}
          }, 
          chain);
          
    TestIncludedObject obj = (TestIncludedObject)IncludeState.createContext("etc/testMainFile.txt", config).include();
    assertTrue(obj.uri.equals("etc/testMainFile.txt"));
    assertEquals(4, obj.children.size());
    assertEquals("testRelativeFileInclude.txt", ((TestIncludedObject)obj.children.get(0)).uri);
    assertEquals("resource:/org/sapia/resource/testUrlResource.txt", ((TestIncludedObject)obj.children.get(1)).uri);    
    assertEquals("file:testRelativeFileInclude.txt", ((TestIncludedObject)obj.children.get(2)).uri);    
       
  }
}
