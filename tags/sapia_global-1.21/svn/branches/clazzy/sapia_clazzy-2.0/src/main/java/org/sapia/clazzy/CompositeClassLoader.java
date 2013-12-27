package org.sapia.clazzy;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.sapia.clazzy.loader.FileSystemLoader;
import org.sapia.clazzy.loader.JarLoader;
import org.sapia.clazzy.loader.Loader;

/**
 * This classloader delegates class lookups the <code>Loader</code> instances
 * that it encapsulates. Children are added through the <code>addLoader()</code> 
 * or <code>addFile</code> method.
 * <p>
 * The <code>CompositeClassLoaderBuilder</code> can conveniently be used to create
 * an instance of this class. 
 * 
 * @see org.sapia.clazzy.CompositeClassLoaderBuilder
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
public class CompositeClassLoader extends BaseClassLoader implements Consts{

  private List           _loaders = Collections.synchronizedList(new ArrayList());
  private LoaderSelector _selector;
  private ResourceBundle _bundle;

  public CompositeClassLoader(ClassLoader parent, LoaderSelector selector) {
    super(parent);
    _selector = selector;
  }

  public CompositeClassLoader(LoaderSelector selector) {
    _selector = selector;
  }
  
  /**
   * @return the URLs to which this instance "points".
   */
  public URL[] getURLs(){
    List urls = new ArrayList();
    Loader loader;
    for(int i = 0; i < _loaders.size(); i++){
      loader = (Loader)_loaders.get(i);
      if(loader instanceof URLEnabled){
        try{
          urls.add(((URLEnabled)loader).getURL());
        }catch(MalformedURLException e){
        }
      }
    }
    return (URL[])urls.toArray(new URL[urls.size()]);
  }

  /**
   * @see java.lang.ClassLoader#findClass(java.lang.String)
   */
  public Class findClass(String name) throws ClassNotFoundException {
    Class clazz = null;
    Loader loader;
    byte[] classBytes = null;
    String resourceName = name.replace('.', '/')+".class";
    synchronized(_loaders) {
      for(int i = 0; i < _loaders.size(); i++) {
        loader = (Loader) _loaders.get(i);
        if(_selector.acceptsClass(name, loader)) {
          classBytes = loader.loadBytes(resourceName);
          if(classBytes != null){
            break;
          }
        }
      }
    }
    if(classBytes == null) {
      throw new ClassNotFoundException(name);
    }
    String pckg = Utils.getPackageNameFor(name);    
    if(pckg != null && getPackage(pckg) == null){
      definePackage(pckg, 
                    PACKAGE_SPEC_TITLE,
                    PACKAGE_SPEC_VERSION,
                    PACKAGE_SPEC_VENDOR,
                    PACKAGE_IMPL_TITLE,
                    PACKAGE_IMPL_VERSION,
                    PACKAGE_IMPL_VENDOR, 
                    null);       
    }
    return super.defineClass(name, classBytes, 0, classBytes.length);
  }
  
  /**
   * @see java.lang.ClassLoader#findResource(java.lang.String)
   */
  protected URL findResource(String name) {
    Loader loader;
    URL toReturn = null;
    synchronized(_loaders) {
      for(int i = 0; i < _loaders.size(); i++) {
        loader = (Loader) _loaders.get(i);
        if(_selector.acceptsResource(name, loader)) {
          toReturn = loader.getURL(name);
          if(toReturn != null){
            break;
          }
        }
      }
    }
    return toReturn;
  }
  
  /**
   * @param loader
   *          the <code>Loader</code> to add to this instance.
   */
  public void addLoader(Loader loader) {
    _loaders.add(loader);
  }
  
  /**
   * Adds the file object corresponding to a directory of classes or
   * a jar to this instance.
   * <p>
   * Internally, the method creates either a <code>FileSystemLoader</code> 
   * (if the given file object corresponds to a directory) or a <code>JarLoader</code>.
   * @param file a <code>File</code>
   */
  public void addPath(File file){
    if(!file.exists()){
      return;
    }
    if(file.isDirectory()){
      addLoader(new FileSystemLoader(file));
    }
    else{
      addLoader(new JarLoader(file));
    }
  }
  
  /**
   * Releases all resources that this instance.
   */
  public void close(){
    Loader loader;
    for(int i = 0; i < _loaders.size(); i++){
      loader = (Loader)_loaders.get(i);
      try{
        loader.close();
      }catch(Exception e){}
    }
  }
  
}
