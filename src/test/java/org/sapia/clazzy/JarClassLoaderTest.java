package org.sapia.clazzy;

import java.io.File;

import org.sapia.clazzy.test.Printable;

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
public class JarClassLoaderTest extends TestCase{
  
  public JarClassLoaderTest(String name){
    super(name);
  }
  
  public void testLoadClass() throws Exception{
    JarClassLoader loader = new JarClassLoader(new File("etc/test/lib/classes.jar"));
    Printable p = (Printable)loader.loadClass("test.JarClass").newInstance();
    super.assertTrue(p.getClass().getPackage() != null);    
    try{
      loader.loadClass("test.None");
      fail("ClassNotFoundException should have been thrown");      
    }catch(ClassNotFoundException e){
      //ok
    }
  }

}
