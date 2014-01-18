package org.sapia.mediafire.core

import java.io.File
import java.util.Map
import collection.JavaConversions._
import java.io.FileInputStream
import java.io.InputStream
import scala.collection.Map
import java.util.Map
import java.lang.Long
import com.google.common.io.Files
import com.google.common.hash.Hashing

/**
 * Default implementation of the MfFacade interface.
 *
 * @param client a MfClient instance.
 *
 * @author yduchesne
 */
class DefaultMfFacade(client: MfClient) extends MfFacade {

  def upload(folderKey: String, files: java.util.Map[String, File], deleteExisting: Boolean, session: MfSession): Unit = {
    for (fileEntry <- files.entrySet()) {
      TracerFactory.getTracer().trace("Uploading file: " + fileEntry.getKey())
      if (deleteExisting) {
        val fileTuple = getFile(folderKey, fileEntry.getKey(), session)
        if (fileTuple != null) {
          deleteFile(fileTuple._1, session)
        }
      }
      
      val hashing = Files.hash(fileEntry.getValue(), Hashing.sha256())
      client.post(
        "upload/upload.php",
        session,
        scala.collection.Map(
          "header:x-filename" -> fileEntry.getKey(),
          "header:x-filesize" -> Long.toString(fileEntry.getValue().length()),
          "header:x-filehash" -> hashing.toString(),
          "uploadkey" -> folderKey),
          fileEntry.getValue())
    }
  }
  
  def getFolderKey(path: String, session: MfSession): String = {
    return getFolderKey(path.split("/"), session)
  }
  
  def getFolderKey(path: Array[String], session: MfSession): String = {
    var folderKey: String = null
    var folderName: String = null

    for (p <- path) {
      val result = 
      if (folderKey == null) {
        doGetFolderKey(p, session)  
      } else {
        doGetChildFolderKey(folderName, folderKey, p, session)
      }
      folderKey  = result._1
      folderName = result._2
    }
    return folderKey
  }

  def deleteFile(quickKey: String, session: MfSession): Unit = {
    client.get("file/delete.php", session, scala.collection.Map("quick_key" -> quickKey))
  }
  
  def getFile(folderKey: String, fileName:String, session: MfSession): (String, String) = {
    val xml = client.get("folder/get_content.php", session, 
        scala.collection.Map("content_type" -> "files", "folder_key" -> folderKey))
    xml \\ "file" foreach { fileElem =>
      val quickKey = (fileElem \ "quickkey").text
      val existing = (fileElem \ "filename").text
      if (existing.equals(fileName)) {
        return (quickKey, fileName)
      }
    }    
    return null;
  }
  
  def getFiles(folderKey: String, session: MfSession): String = {
    val xml = client.get("folder/get_content.php", session, 
        scala.collection.Map("content_type" -> "files", "folder_key" -> folderKey))
    return xml.toString();
  }

  def getFolders(folderKey: String, session: MfSession): String = {
    val xml = client.get("folder/get_content.php", session, 
        scala.collection.Map("content_type" -> "folders", "folder_key" -> folderKey))
    return xml.toString();
  }
  
  private def doGetFolderKey(childFolderName: String, session: MfSession): (String, String) = {
    val xml = client.get("folder/get_content.php", session, scala.collection.Map("content_type" -> "folders"))
    xml \\ "folder" foreach { folderElem =>
      val folderKey = (folderElem \ "folderkey").text
      val folderName = (folderElem \ "name").text
      if (folderName.equals(childFolderName)) {
        return (folderKey, folderName)
      }
    }
    throw new IllegalArgumentException(String.format("No child folder '%s' found", childFolderName))
  }

  private def doGetChildFolderKey(
      parentFolderName: String, 
      parentFolderKey: String, 
      childFolderName: String, 
      session: MfSession): (String, String) = {
    val xml = client.get("folder/get_content.php", session, 
        scala.collection.Map("content_type" -> "folders", "folder_key" -> parentFolderKey))
    xml \\ "folder" foreach { folderElem =>
      val folderKey = (folderElem \ "folderkey").text
      val folderName = (folderElem \ "name").text
      if (folderName.equals(childFolderName)) {
        return (folderKey, folderName)
      }
    }
    throw new IllegalArgumentException(String.format("No child folder '%s' found under folder '%s'", childFolderName, parentFolderName))
  }

}