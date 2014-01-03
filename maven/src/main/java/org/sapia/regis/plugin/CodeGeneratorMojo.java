package org.sapia.regis.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.sapia.regis.Registry;
import org.sapia.regis.RegistryContext;
import org.sapia.regis.codegen.output.CodeGenConfig;
import org.sapia.regis.codegen.output.CodeGenerator;

/**
 * @goal generate
 * 
 * @author yduchesne
 *
 */
public class CodeGeneratorMojo  extends AbstractMojo{

  /**
   * @parameter 
   */
  private String bootstrap;
  
  /**
   * @parameter expression="\${project.build.sourceDirectory}" 
   */  
  private File outputDirectory;
  
  /**
   * @parameter expression="\${project.groupId}.config"
   */
  private String packagePrefix;

  /**
   * @parameter default-value="ConfigFactory"
   */  
  private String rootClass;
  
  /**
   * @parameter expression="\${project.version}"
   */  
  private String version;
  
  /**
   * @parameter default-value="true"
   */  
  private boolean generateGetters = true;
  
  /**
   * @parameter expression="\${project.basedir}"
   */
  private File basedir;
  

  public void execute() throws MojoExecutionException{
    
    getLog().info("bootstrap:      " + bootstrap);
    getLog().info("outputDirectory " + outputDirectory);
    getLog().info("packagePrefix:  " + packagePrefix);
    getLog().info("rootClass:      " + rootClass);
    getLog().info("version:        " + version);
    getLog().info("basedir         " + basedir);
    
    if(bootstrap == null){
      throw new MojoExecutionException("bootstrap not set");
    }
    if(outputDirectory == null){
      throw new MojoExecutionException("outputDirectory not set");
    }    
    if(packagePrefix == null){
      throw new MojoExecutionException("packagePrefix not set");
    }    
    if(rootClass == null){
      throw new MojoExecutionException("rootClass not set");
    }    
    if(version == null){
      throw new MojoExecutionException("config not set"); 
    }
    
    if(!outputDirectory.exists()){
      outputDirectory.mkdirs();
    }
    
    Properties props = new Properties();
    props.setProperty(RegistryContext.BOOTSTRAP, bootstrap);
    
    // making sure we disable variable rendering:
    props.setProperty(RegistryContext.INTERPOLATION_ACTIVE, "false");
    
    // setting user.dir to plugin base dir for rendering
    props.setProperty("user.dir", basedir.getAbsolutePath());
    props.setProperty("basedir", basedir.getAbsolutePath());
    
    RegistryContext ctx = new RegistryContext(props);
    Registry reg = null;
    try{
      reg = ctx.connect();
    }catch(Exception e){
      throw new MojoExecutionException("Could not instantiate registry", e);
    }
    
    CodeGenConfig conf = new CodeGenConfig(outputDirectory);
    conf.setPackagePrefix(packagePrefix);
    conf.setRootClassName(rootClass);
    conf.setVersion(version);
    conf.setGenerateGetters(generateGetters);
    
    CodeGenerator generator = new CodeGenerator(reg, conf);
    try{
      generator.generate();
    }catch(IOException e){
      throw new MojoExecutionException("Error occured while generating source", e);
    }    
  }  
  
}
