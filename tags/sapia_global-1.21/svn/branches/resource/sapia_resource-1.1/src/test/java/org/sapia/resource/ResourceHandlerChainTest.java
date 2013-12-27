package org.sapia.resource;

import junit.framework.TestCase;

public class ResourceHandlerChainTest extends TestCase {
  
  public ResourceHandlerChainTest(String arg0) {
    super(arg0);
  }

  public void testResolveFileResource() throws Exception{
    ResourceHandlerChain chain = new ResourceHandlerChain();
    chain.append(new FileResourceHandler());
    chain.resolveResource("etc/testFileResource.txt");
  }
  
  public void testResolveClasspathResource() throws Exception{
    ResourceHandlerChain chain = new ResourceHandlerChain();
    chain.append(new ClasspathResourceHandler());
    chain.resolveResource("org/sapia/resource/testUrlResource.txt");
  }  
  
  public void testMultiFileResources() throws Exception{
    ResourceHandlerChain chain = new ResourceHandlerChain();
    chain.append(new FileResourceHandler());
    chain.append(new ClasspathResourceHandler());    
    chain.resolveResource("etc/testFileResource.txt");
    chain.resolveResource("org/sapia/resource/testUrlResource.txt");
  }  
}
