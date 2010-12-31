package org.sapia.clazzy;
import java.io.File;

import org.sapia.clazzy.FileSystemClassLoader;
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
public class FileSystemClassLoaderTest extends TestCase{
  
  public FileSystemClassLoaderTest(String name){
    super(name);
  }
  
  public void testLoadClass() throws Exception{
    FileSystemClassLoader loader = new FileSystemClassLoader(new File("etc/test/classes"));
    Printable p = (Printable)loader.loadClass("test.FileSystemClass").newInstance();
    super.assertTrue(p.getClass().getPackage() != null);    
    try{
      loader.loadClass("test.None");
      fail("ClassNotFoundException should have been thrown");
    }catch(ClassNotFoundException e){
      //ok
    }    
  }

}
