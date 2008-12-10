package org.sapia.regis.codegen.output;

import java.io.File;

/**
 * Encapsulates the global code generation configuration
 * 
 * @author yduchesne
 *
 */
public class CodeGenConfig {

  private String packagePrefix, rootClassName;

  private File destinationDir;

  /**
   * @param dest a {@link File} corresponding to the directory where the generated code will
   * be output.
   */
  public CodeGenConfig(File dest){
    this.destinationDir = dest;
  }

  /**
   * @return the package prefix that this instance encapsulates.
   */
  public String getPackagePrefix() {
    return packagePrefix;
  }

  public void setPackagePrefix(String packagePrefix) {
    this.packagePrefix = packagePrefix;
  }

  /**
   * 
   * @return the {@link File} corresponding to the directory where the generated code will
   * be output.
   */
  public File getDestinationDir() {
    return destinationDir;
  }

  /**
   * @return the name of the "root" class whose code will be generated, and will be used by applications.
   */
  public String getRootClassName() {
    return rootClassName;
  }

  public void setRootClassName(String rootClassName) {
    this.rootClassName = rootClassName;
  }

}
