package org.sapia.mediafire.core

/**
 * Holds the Mediafire credentials.
 *
 * @author yduchesne
 */
class MfCredentials(apiKey: String, email: String, password: String, applicationId: String) {

  /**
   * @return the user's API key.
   */
  def getApiKey() = apiKey

  /**
   * @return the user's email.
   */
  def getEmail() = email

  /**
   * @return the user's password
   */
  def getPassword() = password

  /**
   * @return the user's application ID.
   */
  def getApplicationId() = applicationId

}