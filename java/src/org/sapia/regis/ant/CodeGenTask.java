package org.sapia.regis.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.sapia.regis.Registry;
import org.sapia.regis.RegistryContext;
import org.sapia.regis.codegen.output.CodeGenConfig;
import org.sapia.regis.codegen.output.CodeGenerator;

public class CodeGenTask extends Task{
  
  private File propFile, destDir;
  private String packagePrefix, rootClassName, version;
  
  public void setPackagePrefix(String prefix){
    packagePrefix = prefix;
  }
  
  public void setProperties(File f){
    propFile = f;
  }
  
  public void setDestdir(File dir){
    destDir = dir;
  }
  
  public void setRootClassName(String name){
    rootClassName = name;
  }
  
  public void setVersion(String version){
    this.version = version;
  }
  
  public void execute() throws BuildException {
    if(propFile == null){
      throw new BuildException("Regis property file not set");
    }
    if(destDir == null){
      throw new BuildException("destdir not set");
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
      throw new BuildException("Could not load registry properties", e);      
    }
    
    // making sure we disable variable rendering:
    
    props.setProperty(RegistryContext.INTERPOLATION_ACTIVE, "false");
    
    RegistryContext ctx = new RegistryContext(props);
    Registry reg = null;
    try{
      reg = ctx.connect();
    }catch(Exception e){
      throw new BuildException("Could not instantiate registry", e);
    }
    
    CodeGenConfig conf = new CodeGenConfig(destDir);
    conf.setPackagePrefix(packagePrefix);
    conf.setRootClassName(rootClassName);
    conf.setVersion(version);
    
    CodeGenerator generator = new CodeGenerator(reg, conf);
    try{
      generator.generate();
    }catch(IOException e){
      throw new BuildException("Error occured while generating source", e);
    }
  }

}
