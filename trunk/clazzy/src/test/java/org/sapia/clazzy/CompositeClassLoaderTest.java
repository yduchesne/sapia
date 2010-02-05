package org.sapia.clazzy;

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
public class CompositeClassLoaderTest extends TestCase{
  
  public CompositeClassLoaderTest(String name){
    super(name);
  }
  
  public void testLoadClass() throws Exception{
    CompositeClassLoader loader = CompositeClassLoaderBuilder.parseClassPath(null, new DefaultLoaderSelector(), "etc/test/classes:etc/test/lib/classes.jar:etc/test/lib/classes2.jar");
    Printable p = (Printable)loader.loadClass("test.FileSystemClass").newInstance();
    super.assertTrue(p.getClass().getPackage() != null);
    p = (Printable)loader.loadClass("test.JarClass").newInstance();
    super.assertTrue(p.getClass().getPackage() != null);
    p = (Printable)p.getClass().getClassLoader().loadClass("test.JarClass2").newInstance();
    
  }

}
