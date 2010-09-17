package org.sapia.site.plugins.documentation;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Move;
import org.apache.tools.ant.taskdefs.XSLTProcess;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.JavaResource;
import org.apache.tools.ant.util.FileNameMapper;

/**
 * Processes Sapia documentation.
 * 
 * @goal generate
 */
public class DocumentationMojo extends AbstractMojo{

  /**
   * @parameter
   */
  private boolean deleteDestdir = true;
  
  /**
   * @parameter
   */
  private boolean copyResources = true;

  /**
   * @parameter default-value="${basedir}/sapia/site/xml"
   */
  private String basedir;

  
  /**
   * @parameter default-value="${basedir}/target/site"
   */
  private String destdir;

  /**
   * @parameter default-value="**\/*.xdocs,**\/*.xdocs.xml"
   */
  private String includes;
  

  /**
   * @parameter default-value="org/sapia/site/style/sapia.xsl"
   */
  private String xslResource;

  /**
   * @parameter
   */
  private String xslFile;


  public void execute() throws MojoExecutionException{
    Project proj = new Project();
    proj.init();
    deleteDestDir(proj);
    transformXdocs(proj);
    copyResources(proj);
    renameXdocs(proj);
  }
  
  private void deleteDestDir(Project proj) throws MojoExecutionException{
    if(deleteDestdir){
      log("deleting destination dir");
      File destdirFile = new File(destdir);
      if(destdirFile.exists()){
        Delete delete = new Delete();
        delete.setProject(proj);
        delete.setDir(destdirFile);
        delete.setIncludes("**/*");
        
        delete.setTaskName("delete");
        delete.setTaskType("delete");
        delete.setIncludeEmptyDirs(true);
        delete.execute();
      }
    }
  }
  
  private void transformXdocs(Project proj) throws MojoExecutionException{
    log("transforming xdocs files");
    XSLTProcess transform = new XSLTProcess();
    transform.setProject(proj);
    transform.setBasedir(new File(basedir));
    File destdirFile = new File(destdir);
    destdirFile.mkdirs();
    transform.setDestdir(destdirFile);
    transform.setIncludes(includes);
    
    if(xslFile != null){
      transform.setXslResource(new FileResource(new File(xslFile)));
    }
    else{
      JavaResource javaResource = new JavaResource();
      javaResource.setName(xslResource);
      transform.setXslResource(javaResource);
      try{
        File tmpXslt = File.createTempFile("sapia-"+UUID.randomUUID(), ".xsl");
        copyResource(javaResource, tmpXslt);
        transform.setXslResource(new FileResource(tmpXslt));
      }catch(IOException e){
        throw new MojoExecutionException("Could not create temp file", e);
      }
    }
    
    transform.setTaskName("xslt");
    transform.setTaskType("xslt");
    
    transform.execute();
  }
  
  private void copyResources(Project proj) throws MojoExecutionException{
    if(copyResources){
      File basedirFile = new File(basedir);
      File destdirFile = new File(destdir);
      Copy copy = new Copy();
      copy.setProject(proj);
      copy.setTodir(destdirFile);
      
      FileSet include = new FileSet();
      include.setProject(proj);
      include.setDir(basedirFile);
      include.setExcludes(includes);
      copy.addFileset(include);
    
      copy.setTaskName("copy");
      copy.setTaskType("copy");
      
      copy.execute();
      
      Resource sapiaCss = new JavaResource();
      sapiaCss.setName("org/sapia/site/style/sapia.css");
      
      File cssDir = new File(destdirFile, "css");
      cssDir.mkdir();
      this.copyResource(sapiaCss, new File(cssDir, "sapia.css"));
    }
  }

  
  private void renameXdocs(Project proj) throws MojoExecutionException{
    getLog().debug("renaming xdoc.html files");
    
    File destdirFile = new File(destdir);
    Move move = new Move();
    move.setProject(proj);
    move.setTodir(destdirFile);
    
    FileSet include = new FileSet();
    include.setProject(proj);
    include.setDir(destdirFile);
    include.createInclude().setName("**/*.xdocs.html");
    move.addFileset(include);
    move.add(new FileNameMapper() {
      @Override
      public void setTo(String arg0) {
      }
      @Override
      public void setFrom(String arg0) {
      }
      @Override
      public String[] mapFileName(String name) {
        if(name.endsWith("xdocs.html")){
          name = name.replace("xdocs.html", "html");
        }
        return new String[]{ name };
      }
    });
    
    move.setTaskName("move");
    move.setTaskType("move");
    
    move.execute();
    
    // copying home.html to index.html (legacy necessity)
    
    Copy copyHomeFile = new Copy();
    copyHomeFile.setProject(proj);
    copyHomeFile.setTodir(destdirFile);
    
    include = new FileSet();
    include.setProject(proj);
    include.setDir(destdirFile);
    include.setIncludes("**/home.html");
    copyHomeFile.addFileset(include);
    copyHomeFile.add(new FileNameMapper() {
      @Override
      public void setTo(String arg0) {
      }
      @Override
      public void setFrom(String arg0) {
      }
      @Override
      public String[] mapFileName(String name) {
        if(name.endsWith("home.html")){
          name = name.replace("home.html", "index.html");
        }
        return new String[]{ name };
      }
    });  
    
    copyHomeFile.setTaskName("copy");
    copyHomeFile.setTaskType("copy");
    
    copyHomeFile.execute();
    
  }
  

  private void log(String msg){
    getLog().debug(msg);
  }
  
  private void copyResource(Resource from, File to) throws MojoExecutionException{
 
    InputStream is  = null;
    OutputStream os = null;
    
    try{
      BufferedInputStream bis= new BufferedInputStream(is = from.getInputStream());
      os = new FileOutputStream(to);
      byte[] buffer = new byte[2048];
      int read;
      while((read = bis.read(buffer)) > 0){
        os.write(buffer, 0, read);
      }
    }catch(IOException e){
      throw new MojoExecutionException(String.format("Problem transforming docs, could not copy xsl resource %s", from.getName()), e);
    }finally{
      try{ if (is != null) is.close(); }catch(IOException e){}
      try{ if (os != null) os.flush(); os.close(); }catch(IOException e){}
    }
    
  }
  
}
