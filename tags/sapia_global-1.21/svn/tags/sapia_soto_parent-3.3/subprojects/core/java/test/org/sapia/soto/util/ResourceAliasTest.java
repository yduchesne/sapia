package org.sapia.soto.util;

import junit.framework.TestCase;

public class ResourceAliasTest extends TestCase {

  public ResourceAliasTest(String arg0) {
    super(arg0);
  }
  
  public void testMatch() throws Exception{
    ResourceAlias alias = new ResourceAlias();
    alias.setUri("*:/some/uri/test.xml");
    alias.setRedirect("http://other/uri/test.xml");
    super.assertEquals("http://other/uri/test.xml", alias.match("resource:/some/uri/test.xml"));
    
    alias.setRedirect("{1}:/other/uri/test.xml");
    super.assertEquals("resource:/other/uri/test.xml", alias.match("resource:/some/uri/test.xml"));    
  }
  
  public void testMatchNoPattern() throws Exception{
    ResourceAlias alias = new ResourceAlias();
    alias.setUri("resource:/some/uri/test.xml");
    alias.setRedirect("http://other/uri/test.xml");
    super.assertEquals("http://other/uri/test.xml", alias.match("resource:/some/uri/test.xml"));   
  }
  
  public void testMatchNull() throws Exception{
    ResourceAlias alias = new ResourceAlias();
    alias.setRedirect("http://other/uri/test.xml");
    super.assertTrue(alias.match("resource:/some/uri/test.xml") == null);   
  }
  
  public void testNoMatch() throws Exception{
    ResourceAlias alias = new ResourceAlias();
    alias.setUri("resource:/some/uri/test.xml");
    alias.setRedirect("http://other/uri/test.xml");
    super.assertTrue(alias.match("resource:/some/uri/test2.xml") == null);   
  }  

}
