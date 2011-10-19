package org.sapia.soto.util;

import org.sapia.resource.ClasspathResourceHandler;
import org.sapia.resource.FileResourceHandler;
import org.sapia.resource.ResourceHandler;
import org.sapia.resource.UrlResourceHandler;

import junit.framework.TestCase;

/**
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class ResourceHandlerChainTest extends TestCase {

  public ResourceHandlerChainTest(String name) {
    super(name);
  }

  public void testAppend() {
    SotoResourceHandlerChain chain = new SotoResourceHandlerChain();
    String r = "some/resource";
    chain.append(new FileResourceHandler());
    chain.append(new ClasspathResourceHandler());
    ResourceHandler h = chain.select(r);
    super.assertTrue("ResourceHandler not found", h != null);
    super
        .assertTrue("ResourceHandler should be instance of "
            + FileResourceHandler.class.getName(),
            h instanceof FileResourceHandler);
  }

  public void testPrepend() {
    SotoResourceHandlerChain chain = new SotoResourceHandlerChain();
    String r = "some/resource";
    chain.append(new FileResourceHandler());
    chain.prepend(new ClasspathResourceHandler());
    ResourceHandler h = chain.select(r);
    super.assertTrue("ResourceHandler not found", h != null);
    super.assertTrue("ResourceHandler should be instance of "
        + FileResourceHandler.class.getName(),
        h instanceof ClasspathResourceHandler);
  }

  public void testSelect() {
    SotoResourceHandlerChain chain = new SotoResourceHandlerChain();
    String r = "resource:/some/resource";
    chain.append(new FileResourceHandler());
    chain.prepend(new ClasspathResourceHandler());
    ResourceHandler h = chain.select(r);
    super.assertTrue("ResourceHandler not found", h != null);

    r = "file:/some/resource";
    chain.append(new FileResourceHandler());
    chain.prepend(new ClasspathResourceHandler());
    h = chain.select(r);
    super.assertTrue("ResourceHandler not found", h != null);
  }
  
  public void testSelectWithAlias() {
    SotoResourceHandlerChain chain = new SotoResourceHandlerChain();
    
    ResourceAlias alias = new ResourceAlias();
    alias.setRedirect("file:/{1}");
    alias.setUri("resource:/*");
    String r = "resource:/some/resource";
    chain.addResourceAlias(alias);
    chain.append(new FileResourceHandler());
    chain.prepend(new ClasspathResourceHandler());
    chain.append(new UrlResourceHandler());
    ResourceHandler h = chain.select(r);
    super.assertTrue("ResourceHandler not found", h != null);
    super.assertTrue("Expected ClasspathResourceHandler", h instanceof ClasspathResourceHandler);
    super.assertTrue(chain.isCached(r));

    r = "http://some/resource";
    h = chain.select(r);
    super.assertTrue("ResourceHandler not found", h != null);
    super.assertTrue("Expected UrlResourceHandler", h instanceof UrlResourceHandler);    
    super.assertTrue(chain.isCached(r));
  }  

}
