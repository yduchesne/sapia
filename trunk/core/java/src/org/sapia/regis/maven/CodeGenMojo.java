package org.sapia.regis.maven;

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

public class CodeGenMojo  extends AbstractMojo{

  /**
   * @parameter expression="${bootstrapProperties}" 
   */
  private File propFile;

  /**
   * @parameter expression="${outputDirectory}" 
   */  
  private File destDir;
  
  /**
   * @parameter expression="${packagePrefix}" 
   */
  private String packagePrefix;

  /**
   * @parameter expression="${rootClassName}" 
   */  
  private String rootClassName;
  
  /**
   * @parameter expression="${configVersion}" 
   */  
  private String version;
  
  /**
   * @parameter expression="${generateGetters}" default-value=true
   */  
  private boolean generateGetters = true;  
  

  public void execute() throws MojoExecutionException{
    if(propFile == null){
      throw new MojoExecutionException("bootstrapProperties not set");
    }
    if(destDir == null){
      throw new MojoExecutionException("outputDirectory not set");
    }    
    if(packagePrefix == null){
      throw new MojoExecutionException("packagePrefix not set");
    }    
    if(rootClassName == null){
      throw new MojoExecutionException("rootClassName not set");
    }    
    if(version == null){
      throw new MojoExecutionException("configVersion not set");
    }
    
    InputStream is = null;
    Properties props = new Properties();
    try{
      is = new FileInputStream(propFile);
      props.load(is);
    }catch(IOException e){
      try{
        is.close();
      }catch(Exception e2){}
      throw new MojoExecutionException("Could not load registry properties", e);      
    }
    
    // making sure we disable variable rendering:
    
    props.setProperty(RegistryContext.INTERPOLATION_ACTIVE, "false");
    
    RegistryContext ctx = new RegistryContext(props);
    Registry reg = null;
    try{
      reg = ctx.connect();
    }catch(Exception e){
      throw new MojoExecutionException("Could not instantiate registry", e);
    }
    
    CodeGenConfig conf = new CodeGenConfig(destDir);
    conf.setPackagePrefix(packagePrefix);
    conf.setRootClassName(rootClassName);
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
