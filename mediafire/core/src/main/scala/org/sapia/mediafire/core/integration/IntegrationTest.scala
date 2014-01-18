package org.sapia.mediafire.core.integration

import org.sapia.mediafire.core._
import java.util.Map
import java.io.File
import java.util.HashMap

object IntegrationTest {

  def main(args:Array[String]) = {
    val credentials = CredentialsFactory.loadCredentials()
    val client = MfClientFactory.getDefaultClient("https://www.mediafire.com/api")
    val facade = MfClientFactory.getDefaultFacade(client)
    val session = client.createSession(credentials)
    
    val files:Map[String, File] = new HashMap();
    files.put("test.txt", new File("etc/test.txt"));
    val folderKey = facade.getFolderKey("sapiaoss/packages/corus", session)
    facade.upload(folderKey, files, true, session)
    System.out.println(facade.getFiles(folderKey, session))
  }
  
  
}