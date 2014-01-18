package org.sapia.mediafire.core

/**
 * Holds Mediafire session state.
 *
 * @author yduchesne
 */
class MfSession(sessionToken: String) {

  /**
   * @return the session token, as returned by Mediafire upon authentication..
   */
  def getToken() = sessionToken

}