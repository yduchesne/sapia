package org.sapia.mediafire.core

import java.util.Map
import java.io.File

/**
 * Interface that hides the nitty-gritty details of interacting with the Mediafire API.
 * It presents an API that may be used seamlessly by Java clients (uses java.util.Map and java.io.File classes).
 *
 * @author yduchesne
 */
trait MfFacade {

  /**
   * @param folderKey the folder key of the folder to which to upload.
   * @param files the Map of file names-to-files corresponding to the files to upload.
   * @param deleteExisting if true, any existing file for a given file name will be deleted.
   * @param session the current user session.
   */
  def upload(folderKey: String, files: Map[String, File], deleteExisting: Boolean, session: MfSession): Unit

  /**
   * @param path the array of string corresponding to a hierarchical folder list, given from the root.
   * @param session the current user session.
   */
  def getFolderKey(path: Array[String], session: MfSession): String;

  /**
   * @param path the path to a folder, given from the root.
   * @param session the current user session.
   */
  def getFolderKey(path: String, session: MfSession): String;
  
  /**
   * @param quickKey the quick key of the file to delete.
   * @param session the current user session.
   */
  def deleteFile(quickKey: String, session: MfSession): Unit;

  /**
   * @param folderKey the folder key of the folder whose file list should be returned.
   * @return the original Mediafire response to the <code>get_content</code> request, holding the file information.
   */
  def getFiles(folderKey: String, session: MfSession): String;

    /**
   * @param folderKey the folder key of the folder whose folder list should be returned.
   * @return the original Mediafire response to the <code>get_content</code> request, holding the folder information.
   */
  def getFolders(folderKey: String, session: MfSession): String;
  
}