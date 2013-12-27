package org.sapia.soto.xfire.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

public class WsDocGeneratorTask extends Task{
  
  private Path classpath;
  private List<File> sourceDirs = new ArrayList<File>();
  private File destDir;
  
  public void setSourceDir(File dir){
    sourceDirs.add(dir);
  }
  
  public Path createClassPath(){
    classpath = new Path(this.getProject());
    return classpath;
  }
  
  public void setDestDir(File dir){
    destDir = dir;
  }
  
  public void execute() throws BuildException {
    if(destDir == null){
      throw new BuildException("destDir not specified");
    }
    
    if(classpath == null){
      throw new BuildException("classpath not specified");
    }
    
    AntClassLoader loader = new AntClassLoader(getClass().getClassLoader(), getProject(), classpath, false);
    
    WsDocGenerator docgen = new WsDocGenerator(destDir, loader, 
        new TaskLog(){
          public void debug(String msg, int level) {
            log(msg, level);
          }
      });

    for(int i = 0; i < sourceDirs.size(); i++){
      File dir = sourceDirs.get(i);
      if(dir.isDirectory()){
        docgen.addSourceTree(dir);
      }
    }
    
    try{
      docgen.generate();
    }catch(Exception e){
      throw new BuildException("Error generating web service documentation files", e);
    }catch(Error e){
      e.printStackTrace();
    }
    
  }

}
