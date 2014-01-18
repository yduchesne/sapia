package org.sapia.mediafire.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.sapia.mediafire.core.DefaultMfClient;
import org.sapia.mediafire.core.DefaultMfFacade;
import org.sapia.mediafire.core.MfClient;
import org.sapia.mediafire.core.MfCredentials;
import org.sapia.mediafire.core.MfFacade;
import org.sapia.mediafire.core.MfSession;
import org.sapia.mediafire.core.CredentialsFactory;

/**
 * Base class for all Mediafire mojos.
 * 
 */
public abstract class AbstractMediaFireMojo extends AbstractMojo {
  
  /**
   * The maven project.
   * 
   * @parameter expression="${project}" 
   * @readonly
   */
  private MavenProject project;
  
  /**
   * The URL to the Mediafire API (defaults to <code>https://www.mediafire.com/api</code>).
   * 
   * @parameter
   */
  private String url = "https://www.mediafire.com/api";
  
  /**
   * The email of the Mediafire account.
   * 
   * @parameter
   */
  private String email;
  
  /**
   * The password of the Mediafire account.
   * 
   * @parameter
   */
  private String password;
  
  /**
   * The application ID.
   * 
   * @parameter
   */
  private String applicationId;
  
  /**
   * The API key of the Mediafire account.
   * 
   * @parameter
   */
  private String apiKey;
  
  protected MfFacade createFacade(MfClient client) {
    DefaultMfFacade facade = new DefaultMfFacade(client);
    return facade;
  }
  
  protected MfClient createClient() {
    DefaultMfClient client = new DefaultMfClient(url);
    return client;
  }
  
  protected MfSession createSession(MfClient client) {
    MfCredentials credentials;
    if (apiKey == null && email == null && password == null && applicationId == null) {
      credentials = CredentialsFactory.loadCredentials();
    } else {
      if (apiKey == null) {
        throw new IllegalStateException("API key not set");
      }
      if (email == null) {
        throw new IllegalStateException("Email not set");
      }
      if (password == null) {
        throw new IllegalStateException("Password not set");
      }
      if (applicationId == null) {
        throw new IllegalStateException("Application ID not set");
      }
      credentials = new MfCredentials(apiKey, email, password, applicationId);
    }
    
    return client.createSession(credentials);
  }
  
  protected MavenProject getProject() {
    return project;
  }
}
