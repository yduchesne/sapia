package org.sapia.site.plugins.documentation;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.maven.model.MailingList;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Move;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.JavaResource;
import org.apache.tools.ant.util.FileNameMapper;
import org.markdown4j.Markdown4jProcessor;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;

/**
 * Processes Sapia documentation. This plugin expects the documentation 
 * to be in the Markdown format, with files ending with either the <code>mdn</code> 
 * or <code>mdn.txt</code> extension. The documentation is processed using 
 * Markdown4j.
 * <p>
 * The following Maven properties are exported directly as content variables (of the form ${var_name}):
 * 
 * <ul>
 *   <li><code>project.name</code>
 *   <li><code>project.description</code>
 *   <li><code>project.version</code>
 *   <li><code>project.groupId</code>
 *   <li><code>project.artifactId</code>
 *   <li><code>project.inceptionYear</code>
 *   <li><code>project.packaging</code>
 *   <li><code>project.url</code>
 *   <li><code>project.organization.name</code>
 *   <li><code>project.organization.url</code>
 *   <li><code>project.scm.connection</code>
 *   <li><code>project.scm.developerConnection</code>
 *   <li><code>project.scm.url</code>
 *   <li><code>project.scm.tag</code>
 *   <li><code>project.build.directory</code>
 *   <li><code>project.build.outputDirectory</code>
 *   <li><code>project.build.finalName</code>
 * </ul>
 *
 * The following properties correspond to the first <code>mailingList</code> element that appears
 * in the POM:
 * 
 * <ul>
 *   <li><code>project.mailinglist.name</code>
 *   <li><code>project.mailinglist.archive</code>
 *   <li><code>project.mailinglist.subscribe</code>
 *   <li><code>project.mailinglist.unsubscribe</code>
 *   <li><code>project.mailinglist.post</code>
 * </ul>
 * 
 * The following non-Maven properties are also exported:
 * 
 * <ul>
 *   <li><code>build.currentYear</code>
 *   <li><code>build.currentDate</code>
 *   <li><code>build.currentTime</code>
 *   <li><code>build.timestamp</code> (current value of <code>System.currentTimeMillis()</code>)
 *   <li><code>build.username</code> (current value of <code>System.getProperty("user.name")</code>)
 * </ul>
 * 
 * In order to access the value for the above parameters from within a Markdown file, use the <code>${var_name}</code>
 * notation, as specified below:
 * 
 * <pre>
 *   ${build.currentYear}
 * </pre>
 * 
 * @goal processMarkdown
 */
public class MarkdownMojo extends AbstractMojo{

  static final File TEMP_DIR = new File(System.getProperty("java.io.tmpdir"));
  
  /**
   * @parameter
   */
  private boolean deleteDestdir = true;
  
  /**
   * @parameter
   */
  private boolean copyResources = true;

  /**
   * @parameter
   */
  private boolean omitPOM = true;

  /**
   * @parameter default-value="${basedir}/sapia/site/markdown"
   */
  private String basedir;

  
  /**
   * @parameter default-value="${basedir}/target/site"
   */
  private String destdir;

  /**
   * @parameter default-value="**\/*.mdn,**\/*.mdn.txt"
   */
  private String includes;
  

  /**
   * The maven project.
   * 
   * @parameter expression="${project}"
   * @readonly
   */
   private MavenProject project;


  public void execute() throws MojoExecutionException{
    
    if(project.getPackaging() != null && (project.getPackaging().equalsIgnoreCase("pom") && omitPOM)){
      super.getLog().warn(
          String.format(
              "POM packaging specified for project %s, doc plugin execution will be omitted", 
              project.getName() == null ? project.getArtifactId() : project.getName()
          )
      );
      return;
    }
    
    if(!new File(basedir).exists()){
      super.getLog().warn(String.format("Base directory %s not found", basedir));
    }
    else{
      Project proj = new Project();
      proj.init();
      proj.setBaseDir(new File(basedir));
      deleteDestDir(proj);
      processMarkdown(proj);
      copyResources(proj);
      renameMarkdown(proj);
    }
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
  
  private void processMarkdown(Project proj) throws MojoExecutionException{
    log("contexting Markdown files");
    
    Map<String, String> context = new HashMap<String, String>();
    setupParams(context);
  
    File destdirFile = new File(destdir);
    if (!destdirFile.exists()) {
      destdirFile.mkdirs();
    }
    
    FileSet include = new FileSet();
    include.setProject(proj);
    include.setDir(new File(basedir));
    
    DirectoryScanner ds = include.getDirectoryScanner(proj);
    for (String fileName : ds.getIncludedFiles()) {
      File includedFile = new File(ds.getBasedir(), fileName); 
      if (includedFile.exists()) {
        generateMarkdown(includedFile, new File(destdirFile, fileName), context);
      }
    }
    
  }
  
  private void generateMarkdown(File src, File target, Map<String, String> context) throws MojoExecutionException {
    if (src.exists()) {
      try {
        String srcContent = CharStreams.toString(new BufferedReader(new FileReader(src)));
        srcContent = new StrSubstitutor(context).replace(srcContent);
        Markdown4jProcessor processor = new Markdown4jProcessor();
        srcContent = processor.process(srcContent);
        Files.write(srcContent, target, Charsets.UTF_8);
      } catch (IOException e) {
        throw new MojoExecutionException("Cannot process Markdown content for file " + src.getAbsolutePath(), e);
      }
    }
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
      
      
      // copying css resources ///////////////////////////
      File cssDir = new File(destdirFile, "css");
      cssDir.mkdir();
      
      // the css stylesheet      
      copyStyleResource("sapia.css", cssDir);
      
      // the various graphic files
      copyStyleResource("logo-25.png", cssDir);
    }
  }
  
  private void copyStyleResource(String name, File cssDir) throws MojoExecutionException{
    Resource resource = new JavaResource();
    resource.setName("org/sapia/site/style/" + name);
    copyResource(resource, new File(cssDir, name));
  }

  
  private void renameMarkdown(Project proj) throws MojoExecutionException{
    getLog().debug("renaming mdn.txt files");
    
    File destdirFile = new File(destdir);
    Move move = new Move();
    move.setProject(proj);
    move.setTodir(destdirFile);
    
    FileSet include = new FileSet();
    include.setProject(proj);
    include.setDir(destdirFile);
    include.createInclude().setName("**/*.mdn.txt");
    include.createInclude().setName("**/*.mdn");    
    move.addFileset(include);
    move.add(new FileNameMapper() {
      public void setTo(String arg0) {
      }
      public void setFrom(String arg0) {
      }
      public String[] mapFileName(String name) {
        if(name.endsWith("mdn.txt")){
          name = name.replace("mdn.txt", "html");
        } else if(name.endsWith("mdn")){
          name = name.replace("mdn", "html");
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
      public void setTo(String arg0) {
      }
      public void setFrom(String arg0) {
      }
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
  
  
  private void copyResource(Resource from, File to) throws MojoExecutionException {
 
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
      throw new MojoExecutionException(String.format("Could not copy resource %s", from.getName()), e);
    }finally{
      try{ if (is != null) is.close(); }catch(IOException e){}
      try{ if (os != null) os.flush(); os.close(); }catch(IOException e){}
    }
    
  }
  
  private void setupParams(Map<String, String> context){
    
    // project general info
    createParam("project.name", project.getName(), context);
    createParam("project.description", project.getDescription(), context);
    createParam("project.version", project.getVersion().replace("-SNAPSHOT", ""), context);
    createParam("project.artifactId", project.getArtifactId(), context);
    createParam("project.groupId", project.getGroupId(), context);
    createParam("project.inceptionYear", project.getInceptionYear(), context);
    createParam("project.packaging", project.getPackaging(), context);
    createParam("project.url", project.getUrl(), context);
    createParam("project.organization.name", project.getOrganization().getName(), context);

    // organization
    createParam("project.organization.url", project.getOrganization().getUrl(), context);

    // scm
    createParam("project.scm.connection", project.getScm().getConnection(), context);
    createParam("project.scm.developerConnection", project.getScm().getDeveloperConnection(), context);
    createParam("project.scm.url", project.getScm().getUrl(), context);
    createParam("project.scm.tag", project.getScm().getTag(), context);

    // project build info
    createParam("project.build.directory", project.getBuild().getDirectory(), context);
    createParam("project.build.outputDirectory", project.getBuild().getOutputDirectory(), context);
    createParam("project.build.finalName", project.getBuild().getFinalName(), context);
    
    // project mailing lists
    List mailingLists = visit(new ProjectVisitor<List>() {
      public List visit(MavenProject project) {
        if(project.getMailingLists().size() > 0){
          return project.getMailingLists();
        }
        return null;
      }
    });
    if(mailingLists != null && mailingLists.size() > 0){
      MailingList list = (MailingList)mailingLists.get(0);
      createMailingListParams(list, null, context);
    }
    else{
      createMailingListParams(null, null, context);
    }
    // Non-Maven
    Calendar cal = Calendar.getInstance();
    
    createParam("build.currentYear", Integer.toString(cal.get(Calendar.YEAR)), context);
    createParam("build.currentDate", new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()), context);
    createParam("build.currentTime", new SimpleDateFormat("hh:mm:ss").format(cal.getTime()), context);
    createParam("build.timestamp", Long.toString(System.currentTimeMillis()), context);
    createParam("build.username", System.getProperty("user.name"), context);
    
  }
  
  private void createParam(String key, String value, Map<String, String> context){
    String aValue = value == null || value.length() == 0 ? "NOT_FOUND" : value;
    String sysValue = System.getProperty(key);
    if(sysValue != null){
      aValue = sysValue;
    }
    context.put(key, aValue); 
  }
  
  private void createMailingListParams(MailingList list, String index, Map<String, String> context){
    createMailingListParam("project.mailinglist.subscribe", index, list != null ? list.getSubscribe() : null, context);
    createMailingListParam("project.mailinglist.unsubscribe", index, list != null ? list.getUnsubscribe() : null, context);
    createMailingListParam("project.mailinglist.name", index, list != null ? list.getName() : null, context);
    createMailingListParam("project.mailinglist.archive", index, list != null ? list.getArchive() : null, context);
    createMailingListParam("project.mailinglist.post", index, list != null ? list.getPost() : null, context);
  }
  
  private void createMailingListParam(String name, String index, String value, Map<String, String> context){
    String aName = name;
    if(index != null){
      aName = aName+".1";
    }
    createParam(aName, value, context);
  }
  
  private <T> T visit(ProjectVisitor<T> visitor){
    MavenProject current = this.project;
    T value = null;
    do{
      value = visitor.visit(current);
    }while((current = current.getParent()) != null && value == null);
    return value;
  }
  
  interface ProjectVisitor<T>{
    
    
    T visit(MavenProject project);
  }
}
