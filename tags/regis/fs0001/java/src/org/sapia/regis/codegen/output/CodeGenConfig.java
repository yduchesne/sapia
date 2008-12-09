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
  
  public CodeGenConfig(File dest){
    this.destinationDir = dest;
  }

  public String getPackagePrefix() {
    return packagePrefix;
  }

  public void setPackagePrefix(String packagePrefix) {
    this.packagePrefix = packagePrefix;
  }

  public File getDestinationDir() {
    return destinationDir;
  }

  public void setDestinationDir(File destinationDir) {
    this.destinationDir = destinationDir;
  }

  public String getRootClassName() {
    return rootClassName;
  }

  public void setRootClassName(String rootClassName) {
    this.rootClassName = rootClassName;
  }

}
