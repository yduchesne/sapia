package org.sapia.mediafire.mojo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.sapia.mediafire.core.MfClient;
import org.sapia.mediafire.core.MfFacade;
import org.sapia.mediafire.core.MfSession;
import org.sapia.mediafire.core.Tracer;
import org.sapia.mediafire.core.TracerFactory;

/**
 * Gets the content of a given folder (invokes Mediafire's <code>get_content</code> endpoint).
 * 
 * @goal get-content
 */
public class GetContentMojo extends AbstractMediaFireMojo {
	
  public static final String FILES   = "files";
  public static final String FOLDERS = "folders";
  
  /**
   * The list of names of the files to upload.
   *
   * @parameter
   */
  private String contentType = FILES;
  
  /**
   * The path of the Mediafire folder from which to get content.
   * 
   * @parameter
   */
  private String folder;
  
  /**
   * The directory to which to output the XML response obtained from Mediafire.
   * 
   * @parameter
   */
  private String outputDirectory;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    TracerFactory.setTracer(new Tracer() {
      @Override
      public void trace(String msg) {
        getLog().info(msg);
      }
    });
    if (folder == null) {
      throw new MojoExecutionException("Folder from which to get content must be specified");
    }
    
    MfClient  client  = createClient();
    MfSession session = createSession(client);
    MfFacade  facade  = createFacade(client);
    
    String content;
    String folderKey = facade.getFolderKey(folder, session);

    if (contentType.equals(FILES)) {
      content = facade.getFiles(folderKey, session);
    } else {
      content = facade.getFolders(folderKey, session);      
    }
   
    File theOutputDir;
    if (outputDirectory == null) {
      theOutputDir = new File(super.getProject().getBuild().getOutputDirectory());
    } else {
      theOutputDir = new File(outputDirectory);
    }
    if (!theOutputDir.exists() && !theOutputDir.mkdirs()) {
      throw new MojoExecutionException("Could not create output directory: " + theOutputDir.getAbsolutePath());
    }
    
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(theOutputDir);
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
      writer.write(content);
      writer.flush();
      writer.close();
    } catch (IOException e) {
      throw new MojoExecutionException("Could not write content to file system", e);
    } finally {
      if (fos != null)
        try {
          fos.close();
        } catch (IOException e) {
          // noop
        }
    }
  }  
}
