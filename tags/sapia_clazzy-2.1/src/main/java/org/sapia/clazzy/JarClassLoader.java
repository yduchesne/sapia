package org.sapia.clazzy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import org.sapia.clazzy.loader.JarLoader;

/**
 * This class overrides the <code>BaseClassLoader</code> class and search
 * classes in a given jar file. It is provided as an alternative to the 
 * implementation that comes with the JDK, which locks the underlying
 * jar files (in fact keeping the files open). 
 * <p>
 * Under some OS (WIN!"$D%?), this prevents the jar files from being deleted 
 * (for example, in the case of hot-deployments).
 * <p>
 * An instance of this class works around this limitation by internally using
 * a <code>JarLoader</code> instance, which  opens the jar file at instantiation
 * time, and allows closing the said jar file when it is not needed anymore.
 * <p>
 * Client applications should call <code>close()</code> on an instance of this class to ensure
 * that the underlying <code>JarLoader</code> is disposed of cleanly (i.e.: that the jar file
 * it holds is closed).
 * 
 * @author Yanick Duchesne
 * 
 * @see org.sapia.clazzy.loader.JarLoader
 * @see org.sapia.clazzy.ClazzyURLConnection
 * @see org.sapia.clazzy.ClazzyURLStreamHandlerFactory
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
public class JarClassLoader extends BaseClassLoader implements Consts{

  private JarLoader _jar;
  
  ResourceBundle bundle;

  public JarClassLoader(File jar) {
    _jar = new JarLoader(jar);
  }

  public JarClassLoader(ClassLoader parent, File jar) {
    super(parent);
    _jar = new JarLoader(jar);
  }

  /**
   * @return the <code>File</code> corresponding to the archive in which this
   *         instance looks up.
   */
  public File getFile() {
    return _jar.getJarFile();
  }

  /**
   * @see java.lang.ClassLoader#findClass(java.lang.String)
   */
  protected Class findClass(String name) throws ClassNotFoundException {
    if(!_jar.getJarFile().exists()) {
      throw new ClassNotFoundException(name);
    }
    
    String resourceName = name.replace('.', '/') + ".class";
    byte[] classBytes = _jar.loadBytes(resourceName);
    if(classBytes == null){
      throw new ClassNotFoundException(name);
    }
    String pckg = Utils.getPackageNameFor(name);
    if(pckg != null && getPackage(pckg) == null){
      definePackage(pckg, 
                    PACKAGE_IMPL_TITLE,
                    PACKAGE_IMPL_VERSION,
                    PACKAGE_IMPL_VENDOR, 
                    PACKAGE_IMPL_TITLE,
                    PACKAGE_IMPL_VERSION,
                    PACKAGE_IMPL_VENDOR, 
                    null);
    }
    return defineClass(name, classBytes, 0, classBytes.length);
  }
  
  /**
   * @param name the name of the resource whose stream should be returned.
   * @return an <code>InputStream</code>.
   */
  public InputStream findResourceAsStream(String name){
    if(!_jar.getJarFile().exists()) {
      return null;
    }
    byte[] toReturn = _jar.loadBytes(name);
    if(toReturn == null){
      return null;
    }
    return new ByteArrayInputStream(toReturn);
  }
  
  /**
   * @see java.lang.ClassLoader#findResource(java.lang.String)
   */
  protected URL findResource(String name) {
    try{
      return new URL(ClazzyURLStreamHandlerFactory.PROTOCOL, "", _jar.getJarFile().getAbsolutePath()+"?"+name);
    }catch(MalformedURLException e){
      return null;
    }
  }
  
  /**
   * @see JarLoader#close()
   */
  public void close(){
    _jar.close();
  }
  
}
