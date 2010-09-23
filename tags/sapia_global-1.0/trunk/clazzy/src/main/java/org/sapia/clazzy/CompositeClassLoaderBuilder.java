package org.sapia.clazzy;

import java.io.File;
import java.util.StringTokenizer;

import org.sapia.clazzy.loader.FileSystemLoader;
import org.sapia.clazzy.loader.JarLoader;

/**
 * This class is a utility that parses a classpath and returns the corresponding classloader.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class CompositeClassLoaderBuilder {
  
  /**
   * Builds a composite classloader, given a classpath (passed in as a string). 
   * The classpath format is as follows:
   * <pre>
   * some.jar:path/to/some/directory
   * </pre>
   * or as follows:
   * <pre>
   * some.jar;path/to/some/directory
   * </pre>
   * Both ';' and ':' are properly understood as path delimiters.
   * <p>
   * In addition, any backslashes ('\') in file paths are replaced by the
   * system file separator (<code>System.getProperty("file.separator")</code>).
   * 
   * @param parent a parent <code>ClassLoader</code>.
   * @param selector a <code>LoaderSelector</code>.
   * @param classpath a classpath
   * @return a <code>CompositeClassLoader</code>.
   */
  public static CompositeClassLoader parseClassPath(ClassLoader parent, LoaderSelector selector, String classpath){
    StringTokenizer tokenizer = new StringTokenizer(classpath, ":;");
    String path;
    File f;
    CompositeClassLoader loader;
    if(parent != null) loader = new CompositeClassLoader(parent, selector);
    else loader = new CompositeClassLoader(Thread.currentThread().getContextClassLoader(), selector);
    while(tokenizer.hasMoreTokens()){
      path = tokenizer.nextToken();
      path = path.replace('\\', File.separatorChar);
      f = new File(path);
      if(f.isDirectory()){
        loader.addLoader(new FileSystemLoader(f));
      }
      else{
        loader.addLoader(new JarLoader(f));
      }
    }
    return loader;
  }

}
