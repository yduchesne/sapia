/**
 * 
 */
package org.sapia.soto.me;

import java.io.IOException;

import javolution.util.FastMap;

import org.sapia.soto.me.model.J2meServiceMetaData;
import org.sapia.soto.me.net.Resource;
import org.sapia.soto.me.xml.J2meProcessingException;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public interface MIDletEnv {

  /**
   * Returns the value of the property as it exists in the MIDlet environment. The retrieve operation
   * will look at the following levels to find a value:
   * <ol>
   *   <li>Local properties that could be set in the SotoMe enviroment</li>
   *   <li>Properties that exists in the MIDlet JAD descriptor</li>
   *   <li>Global system properties of the VM</li>  
   * </ol>
   *   
   * @param aName The name of the property to retrieve.
   * @return The value of the property found, or <code>null</code> if no property is found.
   */
  public String getProperty(String aName);
  
  /**
   * Adds the property name and value passed in to the SotoMe environement (local container).
   * 
   * @param aName The name of the property to set.
   * @param aValue The value of the property.
   */
  public void setProperty(String aName, String aValue);
  
  /**
   * Lookup a service given the identifier passed in.
   * 
   * @param anObjectId The identifier of the object to lookup.
   * @return The service retrieved.
   * @exception NotFoundException If no service is found for the given name.
   */
  public J2meService lookupService(String anObjectId) throws NotFoundException;
  
  /**
   * Binds an object under a given identifier. That object can be retrieved
   * later on through configuration by using its identifier with the method
   * {@link #lookupObject(String)}.
   *
   * @param anId an arbitrary identifier.
   * @param anObject the <code>Object</code> to bind.
   * @see #lookupObject(String);
   */
  public void bindObject(String anId, Object anObject);
  
  /**
   * Lookup an object previously bound to a given name using the {@link #bindObject(String, Object)} method.
   *  
   * @param anObjectId The identifier under which the expected object was registered.
   * @return the <code>Object</code> corresponding to the given identifier.
   * @see #bindObject(String, Object);
   */
  public Object lookupObject(String anObjectId) throws NotFoundException;

  /**
   * Resolves the resource corresponding to the URI passed in.
   * 
   * @param aResourceUri The URI of the resource to resolve.
   * @return the {@link Resource} corresponding to the given uri.
   */
  public Resource resolveResource(String aResourceUri) throws IOException;
  
  /**
   * Includes a resource based on its name and the current properties of this SotoMe container.
   * 
   * @param aResource The name of the resource to include.
   * @return The object included.
   */
  public Object include(Resource aResource) throws IOException, J2meProcessingException;
  
  /**
   * Includes a resource based on its name and the properties of this SotoMe container.
   * 
   * @param aResource The name of the resource to include.
   * @param someProperties The properties to use to include the resource.
   * @return The object included.
   */
  public Object include(Resource aResource, FastMap someProperties) throws IOException, J2meProcessingException;

  /**
   * Retrieves the meta data of the passed in service.
   * 
   * @param aService The service instance.
   * @return The meta data object found.
   * @throws NotFoundException If the service passed in is not found.
   */
  public J2meServiceMetaData getMetaDataFor(J2meService aService) throws NotFoundException;
  
  /**
   * Request the current MIDlet environment to shutdown the container and to dispose all services.
   */
  public void requestShutdown();
}
