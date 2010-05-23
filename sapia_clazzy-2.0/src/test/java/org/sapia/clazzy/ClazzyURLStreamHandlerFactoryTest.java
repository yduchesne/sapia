package org.sapia.clazzy;

import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ClazzyURLStreamHandlerFactoryTest extends TestCase{
  
  public ClazzyURLStreamHandlerFactoryTest(String name){
    super(name);
  }
  
  public void testCreateURLStreamHandler() throws Exception{
    ClazzyURLStreamHandlerFactory fac = new ClazzyURLStreamHandlerFactory();
    Handler handler = (Handler)fac.createURLStreamHandler("clazzy");
  }
  
  public void testURLStreamHandlerFactoryBehavior() throws Exception{
    System.setProperty("java.protocol.handler.pkgs", "org.sapia");
    URL resource = new URL("clazzy:etc/test/lib/classes.jar?test/resource.txt");
    InputStream is = resource.openStream();
    is.close();    
    
    ClazzyURLStreamHandlerFactory fac = new ClazzyURLStreamHandlerFactory();
    URL.setURLStreamHandlerFactory(fac);
    resource = new URL("clazzy:etc/test/lib/classes.jar?test/resource.txt");
    is = resource.openStream();
    is.close();
    
    CompositeClassLoader loader = CompositeClassLoaderBuilder.parseClassPath(null, new DefaultLoaderSelector(), "etc/test/lib/classes.jar");
    URL url = loader.getResource("test/resource.txt");
    super.assertTrue("Could not find resource", url != null);
  }

}
