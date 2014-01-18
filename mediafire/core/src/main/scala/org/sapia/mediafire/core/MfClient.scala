package org.sapia.mediafire.core

import java.io.InputStream
import scala.collection.Map
import scala.xml.Elem
import java.io.File

/**
 * Abstracts access to the Mediafire API.
 *
 * @author yduchesne
 */
trait MfClient {

  /**
   * @param credentials the credentials to use.
   * @return a new MfSession.
   */
  def createSession(credentials: MfCredentials): MfSession
 
  /**
   * @param path the request path, relative to the base URL.
   * @param session the current user session.
   * @param params request parameters, if any.
   * @param content the file holding the content to post.
   * @return the XML response content.
   */
  def post(path: String, session: MfSession, params: Map[String, String], content: File): Elem

  /**
   * @param path the request path, relative to the base URL.
   * @param session the current user session.
   * @param params request parameters, if any.
   * @return the XML response content.
   */
  def get(path: String, session: MfSession, params: Map[String, String]): Elem

}