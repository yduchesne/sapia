package org.sapia.regis.codegen.output;

import java.io.File;

/**
 * Encapsulates the global code generation configuration
 * 
 * @author yduchesne
 *
 */
public class CodeGenConfig {
  
  public static final String VERSION_UNSPECIFIED = "unspecified";

  private String packagePrefix, rootClassName;

  private File destinationDir;
  
  private String version;
  
  private boolean generateGetters;

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

  /**
   * @return the version that should be inserted into the generated sources.
   */
  public String getVersion() {
    if(version == null){
      return VERSION_UNSPECIFIED; 
    }
    else{
      return version;
    }
  }

  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * @return <code>true</code> if getters should be generated as accessors.
   */
  public boolean isGenerateGetters() {
    return generateGetters;
  }
  
  public void setGenerateGetters(boolean generateGetters) {
    this.generateGetters = generateGetters;
  }
}
