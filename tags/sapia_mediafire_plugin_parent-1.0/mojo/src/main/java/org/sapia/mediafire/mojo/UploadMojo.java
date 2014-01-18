package org.sapia.mediafire.mojo;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.sapia.mediafire.core.MfClient;
import org.sapia.mediafire.core.MfFacade;
import org.sapia.mediafire.core.MfSession;
import org.sapia.mediafire.core.Tracer;
import org.sapia.mediafire.core.TracerFactory;

/**
 * Performs upload of given files to a specified Mediafire folder.
 * 
 * @goal upload
 * @phase deploy
 */
public class UploadMojo extends AbstractMediaFireMojo {
	
  /**
   * The list of names of the files to upload.
   *
   * @parameter
   */
  private List<String> files;
  
  /**
   * The path to the Mediafire folder to which to upload (relative to the root of the user's Mediafire folder tree).
   * 
   * @parameter
   */
  private String folder;
  
  /**
   * Indicates if an already existing file in Mediafire should be deleted (<code>true</code>).
   * 
   * @parameter expression="${deleteExisting}"
   */
  private boolean deleteExisting = true;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    TracerFactory.setTracer(new Tracer() {
      @Override
      public void trace(String msg) {
        getLog().info(msg);
      }
    });
    
    MfClient  client  = createClient();
    MfSession session = createSession(client);
    MfFacade  facade  = createFacade(client);
    
    Map<String, File> fileMap = new HashMap<>();
    
    for (String fname : files) {
      File toUpload = new File(fname);
      if (!toUpload.exists()) {
        throw new MojoFailureException("File does not exist (could not complete upload): " + toUpload.getAbsolutePath());
      }
      fileMap.put(toUpload.getName(), toUpload);
    }
 
    String folderKey = facade.getFolderKey(folder, session);
    facade.upload(folderKey, fileMap, deleteExisting, session);
  }  
}
