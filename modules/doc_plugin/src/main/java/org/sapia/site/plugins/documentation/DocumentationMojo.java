package org.sapia.site.plugins.documentation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.apache.maven.model.MailingList;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Move;
import org.apache.tools.ant.taskdefs.XSLTProcess;
import org.apache.tools.ant.taskdefs.XSLTProcess.Param;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.JavaResource;
import org.apache.tools.ant.util.FileNameMapper;

/**
 * Processes Sapia documentation. This plugin expects the documentation 
 * to be in well-formed XML files ending either with the <code>xdocs</code> 
 * or <code>xdocs.xml</code> extension. The documentation is processed using 
 * the Sapia XSL stylesheet, which interprets (and transforms) custom XML elements 
 * pertaining to Sapia's documentation.
 * <p>
 * The following Maven properties are exported directly as XSL stylesheet parameters:
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
 * In order to access the value for the above parameters from within xdocs files, use a <code>sapia:param</code>
 * element, as specified below:
 * 
 * <pre>
 *   &lt;sapia:param value="build.currentYear" /&gt;
 * </pre>
 * 
 * @goal generate
 */
public class DocumentationMojo extends AbstractMojo{

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
   * @parameter
   */
  private boolean generateDownloadPage = true; 

  /**
   * @parameter
   */
  private boolean generateMailingListPage = true; 

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
      deleteDestDir(proj);
      transformXdocs(proj);
      copyResources(proj);
      renameXdocs(proj);
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
  
  private void transformXdocs(Project proj) throws MojoExecutionException{
    log("transforming xdocs files");
    File xsltFile = getXsltFile(); 
    XSLTProcess mainTransform = createXslt(proj, xsltFile, new File(basedir), this.includes);
    mainTransform.execute();
 
    File downloadPage = new File(basedir, "download.xdocs.xml");
    if(generateDownloadPage && !downloadPage.exists()){
      transformBuiltinXdoc(proj, xsltFile, downloadPage.getName());
    }
    
    File mailingListPage = new File(basedir, "list.xdocs.xml");
    if(generateMailingListPage && !mailingListPage.exists()){
      transformBuiltinXdoc(proj, xsltFile, mailingListPage.getName());
    }
  }
  
  private void transformBuiltinXdoc(Project proj, File xsltFile, String builtInXdocName) throws MojoExecutionException{
    File tmpBuiltinPageFile = this.createTempFile(builtInXdocName);
    JavaResource builtInPageResource = new JavaResource();
    builtInPageResource.setName("org/sapia/site/content/"+builtInXdocName);
    copyResource(builtInPageResource, tmpBuiltinPageFile);
    XSLTProcess downloadPageTransform = createXslt(proj, xsltFile, TEMP_DIR, tmpBuiltinPageFile.getName());
    downloadPageTransform.execute();
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
      copyStyleResource("gradient-blue.jpg", cssDir);
      copyStyleResource("img01.jpg", cssDir);
      copyStyleResource("img02-green.jpg", cssDir);
      copyStyleResource("img03.gif", cssDir);
      copyStyleResource("img05.jpg", cssDir);
      copyStyleResource("logo-25.png", cssDir);
    }
  }
  
  private void copyStyleResource(String name, File cssDir) throws MojoExecutionException{
    Resource resource = new JavaResource();
    resource.setName("org/sapia/site/style/" + name);
    copyResource(resource, new File(cssDir, name));
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
      public void setTo(String arg0) {
      }
      public void setFrom(String arg0) {
      }
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

  private File getXsltFile() throws MojoExecutionException{
    File toReturn = null;
    if(xslFile != null){
      new File(xslFile);
    }
    else{
      JavaResource javaResource = new JavaResource();
      javaResource.setName(xslResource);
      File tmpXslt = this.createTempFile("sapia-"+UUID.randomUUID()+".xsl");
      copyResource(javaResource, tmpXslt);
      return tmpXslt;
    } 
    return toReturn;
  }
  
  private XSLTProcess createXslt(Project proj, File xslt, File basedir, String includes) throws MojoExecutionException{
    XSLTProcess transform = new XSLTProcess();
    transform.setProject(proj);
    transform.setBasedir(basedir);
    File destdirFile = new File(destdir);
    destdirFile.mkdirs();
    transform.setDestdir(destdirFile);
    transform.setIncludes(includes);
    setXslParams(transform);
    transform.setXslResource(new FileResource(xslt));
    transform.setTaskName("xslt");
    transform.setTaskType("xslt");
    return transform;
  }
  
  private void setXslParams(XSLTProcess transform){
    
    // project general info
    createParam("project.name", project.getName(), transform);
    createParam("project.description", project.getDescription(), transform);
    createParam("project.version", project.getVersion().replace("-SNAPSHOT", ""), transform);
    createParam("project.artifactId", project.getArtifactId(), transform);
    createParam("project.groupId", project.getGroupId(), transform);
    createParam("project.inceptionYear", project.getInceptionYear(), transform);
    createParam("project.packaging", project.getPackaging(), transform);
    createParam("project.url", project.getUrl(), transform);
    createParam("project.organization.name", project.getOrganization().getName(), transform);

    // organization
    createParam("project.organization.url", project.getOrganization().getUrl(), transform);

    // scm
    createParam("project.scm.connection", project.getScm().getConnection(), transform);
    createParam("project.scm.developerConnection", project.getScm().getDeveloperConnection(), transform);
    createParam("project.scm.url", project.getScm().getUrl(), transform);
    createParam("project.scm.tag", project.getScm().getTag(), transform);

    // project build info
    createParam("project.build.directory", project.getBuild().getDirectory(), transform);
    createParam("project.build.outputDirectory", project.getBuild().getOutputDirectory(), transform);
    createParam("project.build.finalName", project.getBuild().getFinalName(), transform);
    
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
      createMailingListParams(list, null, transform);
    }
    else{
      createMailingListParams(null, null, transform);
    }
    // Non-Maven
    Calendar cal = Calendar.getInstance();
    
    createParam("build.currentYear", Integer.toString(cal.get(Calendar.YEAR)), transform);
    createParam("build.currentDate", new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()), transform);
    createParam("build.currentTime", new SimpleDateFormat("hh:mm:ss").format(cal.getTime()), transform);
    createParam("build.timestamp", Long.toString(System.currentTimeMillis()), transform);
    createParam("build.username", System.getProperty("user.name"), transform);
    
  }
  
  private void createParam(String key, String value, XSLTProcess transform){
    
    
    String aValue = value == null || value.length() == 0 ? "NOT_FOUND" : value;
    String sysValue = System.getProperty(key);
    if(sysValue != null){
      aValue = sysValue;
    }
    Param p = transform.createParam();
    p.setName(key);
    p.setExpression(aValue);
   
  }
  
  private void createMailingListParams(MailingList list, String index, XSLTProcess transform){
    createMailingListParam("project.mailinglist.subscribe", index, list != null ? list.getSubscribe() : null, transform);
    createMailingListParam("project.mailinglist.unsubscribe", index, list != null ? list.getUnsubscribe() : null, transform);
    createMailingListParam("project.mailinglist.name", index, list != null ? list.getName() : null, transform);
    createMailingListParam("project.mailinglist.archive", index, list != null ? list.getArchive() : null, transform);
    createMailingListParam("project.mailinglist.post", index, list != null ? list.getPost() : null, transform);
  }
  
  private void createMailingListParam(String name, String index, String value, XSLTProcess transform){
    String aName = name;
    if(index != null){
      aName = aName+".1";
    }
    createParam(aName, value, transform);
  }
  
  private File createTempFile(String name){
    File tmp = new File(TEMP_DIR, name);
    try{
      tmp.createNewFile();
    }catch(IOException e){
      throw new IllegalStateException(
          String.format("Could not create temp file: %s", tmp.getAbsolutePath())
      );
    }
    return tmp;
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
