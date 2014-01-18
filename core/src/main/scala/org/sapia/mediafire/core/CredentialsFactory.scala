package org.sapia.mediafire.core

import java.io.File
import java.util.Properties
import java.io.InputStream
import java.io.FileInputStream

/**
 * A factory class used to obtain MfCredentials instances.
 */
object CredentialsFactory {

  /**
   * Loads the Mediafire credentials from a properties file (<code>mediafire.properties</code>) under
   * <code>$user.home</code> or under <code>$user.home/.mediafire</code>.
   */
  def loadCredentials(): MfCredentials = {
    val userHome = new File(System.getProperty("user.home"))
    var mediaFirePropFile = new File(userHome, "mediafire.properties")
    if (!mediaFirePropFile.exists()) {
      mediaFirePropFile = new File(userHome.getAbsolutePath() 
          + File.separator +  ".mediafire" + File.separator + "mediafire.properties");
      if (!mediaFirePropFile.exists()) {
        throw new IllegalStateException("Could not find mediafire.properties under user home")
      }
    }
    val props = new Properties()
    val is = new FileInputStream(mediaFirePropFile)
    try {
      props.load(is)
    } finally {
      is.close()
    }
    
    val apiKey = props.getProperty("mediafire.api.key")
    assert(apiKey != null, "mediafire.api.key property not specified")
    
    val email = props.getProperty("mediafire.email")
    assert(email != null, "mediafire.email property not specified")
    
    val password = props.getProperty("mediafire.password")   
    assert(password != null, "mediafire.passsword property not specified")
    
    val applicationId = props.getProperty("mediafire.application.id")
    assert(applicationId != null, "mediafire.application.id property not specified")
    
    return new MfCredentials(apiKey.trim(), email.trim(), password.trim(), applicationId.trim())
  }
}