package org.sapia.clazzy;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.sapia.clazzy.loader.FileSystemLoader;

/**
 * This class overrides the <code>BaseClassLoader</code> class and search
 * classes in a given directory.
 * 
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
public class FileSystemClassLoader extends BaseClassLoader implements Consts{

  private FileSystemLoader _loader;

  public FileSystemClassLoader(File baseDir) {
    _loader = new FileSystemLoader(baseDir);
  }

  public FileSystemClassLoader(ClassLoader parent, File baseDir) {
    super(parent);
    _loader = new FileSystemLoader(baseDir);
  }

  /**
   * @return the <code>File</code> corresponding to the base directory in
   *         which this instance looks up.
   */
  public File getFile() {
    return _loader.getBaseDir();
  }

  /**
   * @see java.lang.ClassLoader#findClass(java.lang.String)
   */
  protected Class findClass(String name) throws ClassNotFoundException {
    if(!_loader.getBaseDir().exists() || !_loader.getBaseDir().isDirectory()) {
      throw new ClassNotFoundException(name);
    }
    String className = name.replace('.', File.separatorChar)+".class";

    
    byte[] classBytes = _loader.loadBytes(className);
    if(classBytes == null){
      throw new ClassNotFoundException(name);
    }
    String pckg = Utils.getPackageNameFor(name);    
    if(pckg != null && getPackage(pckg) == null)
      definePackage(pckg, 
                    PACKAGE_SPEC_TITLE,
                    PACKAGE_SPEC_VERSION,
                    PACKAGE_SPEC_VENDOR,
                    PACKAGE_IMPL_TITLE,
                    PACKAGE_IMPL_VERSION,
                    PACKAGE_IMPL_VENDOR, 
                    null);    
    return defineClass(name, classBytes, 0, classBytes.length);
  }
  
  /**
   * @see java.lang.ClassLoader#findResource(java.lang.String)
   */
  protected URL findResource(String name) {
    if(!_loader.getBaseDir().exists() || !_loader.getBaseDir().isDirectory()) {
      return null;
    }
    try{
      File f = new File(_loader.getBaseDir(), name);
      if(f.exists() && f.isFile()){
        return f.toURL();
      }
      return null;
    }catch(IOException e){
      return null;
    }
  }
}
