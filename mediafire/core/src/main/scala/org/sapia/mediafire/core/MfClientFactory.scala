package org.sapia.mediafire.core

/**
 * Factory class for acquiring a MfClient and MfFacade instances.
 *
 * @author yduchesne
 */
object MfClientFactory {

  val MEDIAFIRE_API_URL = "https://www.mediafire.com/api";
  
  /**
   * @param baseUrl the base URL to connect to.
   */
  def getDefaultClient(baseUrl: String): MfClient = {
    return new DefaultMfClient(baseUrl);
  }
  
  /**
   * Returns a client that connects to the default Mediafire API url.
   */
  def getDefaultClient() : MfClient = {
    return getDefaultClient(MEDIAFIRE_API_URL)
  }

  /**
   * @param client the MfClient that the returned facade should wrap.
   */
  def getDefaultFacade(client: MfClient): MfFacade = {
    return new DefaultMfFacade(client)
  }
}