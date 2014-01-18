package org.sapia.mediafire.core

import javax.ws.rs.client.Client
import javax.ws.rs.core.UriBuilder
import java.net.URI
import javax.ws.rs.client.Invocation
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.MediaType
import java.lang.Double
import javax.ws.rs.client.ClientBuilder
import java.io.InputStream
import scala.collection.Map
import javax.ws.rs.client.Entity
import scala.xml.Elem
import com.google.common.hash.Hashing
import java.io.File
import java.io.BufferedInputStream
import java.io.FileInputStream
import org.glassfish.jersey.client.ClientResponse
import javax.ws.rs.core.Response

/**
 * A default implementation of the <code>MfClient</code> trait: the implementation makes use of the
 * Jersey REST client.
 *
 * @author yduchesne
 */
class DefaultMfClient(baseUrl: String) extends MfClient {

  val BUFFER_SIZE = 8000
  
  val client = ClientBuilder.newClient()

  def createSession(credentials: MfCredentials): MfSession = {
    val request = client.target(new URI(baseUrl + "/user/get_session_token.php"))
      .queryParam("email", credentials.getEmail())
      .queryParam("password", credentials.getPassword())
      .queryParam("version", "1")
      .queryParam("application_id", credentials.getApplicationId)
      .queryParam(
          "signature", 
          Hashing.sha1().hashBytes(
              (credentials.getEmail()
              + credentials.getPassword()
              + credentials.getApplicationId()
              + credentials.getApiKey()).getBytes()
          )
      );

    val response = request.request(MediaType.TEXT_XML).get()

    if (response.getStatus() != HttpStatus.OK.id) {
      val msg = "Could not authenticate: %s (status: %s)".format(
        response.getStatusInfo().getReasonPhrase(),
        response.getStatus())
      throw new IllegalStateException(msg);
    }
    
    val stream = response.readEntity(classOf[InputStream])
    try {
      val xml    = scala.xml.XML.load(stream)
      val result = xml \ "result"
      if (result.text != "Success") {
        throw new IllegalStateException("Could not authenticate");
      }
      val sessionToken = xml \\ "session_token"
   
      return new MfSession(sessionToken.text)
    } finally {
      stream.close()
    }
  }

  def post(path: String, session: MfSession, params: Map[String, String], content: File): Elem = {
    var request = client.target(new URI(baseUrl + "/" + path))
    request     = populateAuthParams(request, session)
    request     = processQueryParams(request, params) 
  
    var mimeType = if (params.contains("header:Content-Type")) params.get("header:Content-Type").get else MediaType.APPLICATION_OCTET_STREAM
    val invocation = request.request(MediaType.TEXT_XML)
    processHeaders(invocation, params)
    
    val contentStream = new BufferedInputStream(new FileInputStream(content), BUFFER_SIZE)
    var response: Response = null;
    try {
      response = invocation.buildPost(Entity.entity(contentStream, mimeType)).invoke();
      if (response.getStatus() != HttpStatus.OK.id) {
        val msg = "Could not perform POST request: %s (status: %s). Request path: %s".format(
          response.getStatusInfo().getReasonPhrase(),
          response.getStatus(),
          baseUrl + "/" + path)
        throw new IllegalStateException(msg)
      }
    } finally {
      contentStream.close();
    }

    val stream = response.readEntity(classOf[InputStream])
    try {
      val xml    = scala.xml.XML.load(stream)
      val result = xml \ "result"
      if (result.text != "Success") {
        throw new IllegalStateException("Could not perform POST request: " + xml.toString)
      }
      return xml
    } finally {
      stream.close()
    }
  }

  def get(path: String, session: MfSession, params: Map[String, String]): Elem = {
    var request = client.target(new URI(baseUrl + "/" + path))
    request     = populateAuthParams(request, session)
    request     = processQueryParams(request, params)
    val invocation = request.request(MediaType.TEXT_XML)
    processHeaders(invocation, params)

    val response = invocation.get()
    if (response.getStatus() != HttpStatus.OK.id) {
      val msg = "Could not perform GET request: %s (status: %s)".format(
        response.getStatusInfo().getReasonPhrase(),
        response.getStatus())
      throw new IllegalStateException(msg);
    }

    val stream = response.readEntity(classOf[InputStream])
    try {
      val xml    = scala.xml.XML.load(stream)
      val result = xml \ "result"
      if (result.text != "Success") {
        throw new IllegalStateException("Could not perform GET request " + xml.toString())
      }
      return xml
    } finally {
      stream.close()
    }
  }

  // --------------------------------------------------------------------------
  // restricted methods

  private def processHeaders(request: Invocation.Builder, params: Map[String, String]): Unit = {
    params.foreach({ tuple =>
      if (tuple._1.startsWith("header:")) {
        val header = tuple._1.substring(tuple._1.indexOf(":") + 1);
        request.header(header, tuple._2)
      }
    })
  }

  private def processQueryParams(request: WebTarget, params: Map[String, String]): WebTarget = {
    var toReturn: WebTarget = request
    params.foreach({ tuple =>
      if (!tuple._1.startsWith("header:")) {
        toReturn = toReturn.queryParam(tuple._1, tuple._2)
       }
    })
    return toReturn
  }

  private def populateAuthParams(request: WebTarget, session: MfSession): WebTarget = {
    return request.queryParam("session_token", session.getToken)
  }
}