package org.sapia.soto;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.sapia.resource.Resource;
import org.sapia.resource.ResourceHandler;
import org.sapia.soto.util.SotoResourceHandlerChain;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectFactoryIF;

/** 
 * An instance of this interface provides an indirect hook into the container.
 * 
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2003 <a
 *         href="http://www.sapia-oss.org">Sapia Open Source Software </a>. All
 *         Rights Reserved.</dd>
 *         </dt>
 *         <dt><b>License: </b>
 *         <dd>Read the license.txt file of the jar or visit the <a
 *         href="http://www.sapia-oss.org/license.html">license page </a> at the
 *         Sapia OSS web site</dd>
 *         </dt>
 *         </dl>
 */
public interface Env {
  /**
   * @see SotoContainer#lookup(String)
   */
  public Object lookup(String serviceId) throws NotFoundException;

  /**
   * @see SotoContainer#lookup(Class)
   */
  public Object lookup(Class instanceOf) throws NotFoundException;

  /**
   * @see SotoContainer#lookup(ServiceSelector, boolean)
   */
  public List lookup(ServiceSelector selector, boolean returnMetadata);

  /**
   * @see SotoContainer#getResourceHandlers()
   */
  public SotoResourceHandlerChain getResourceHandlers();

  /**
   * @see SotoContainer#getResourceHandlerFor(String)
   */
  public ResourceHandler getResourceHandlerFor(String protocol);

  /**
   * Resolves the resource corresponding to the given URI. The path can have a
   * protocol/scheme.
   * 
   * @param uri
   *          a URI.
   * @return the <code>InputStream</code> corresponding to the given resource
   *         path.
   */
  public InputStream resolveStream(String uri) throws IOException;

  /**
   * Resolves the resource corresponding to the given URI. The path can have a
   * protocol/scheme.
   * 
   * @param uri
   *          a URI.
   * @return the <code>Resource</code> corresponding to the given uri.
   */
  public Resource resolveResource(String uri) throws IOException;

  /**
   * Returns the object factory that is used to create objects from XML
   * configuration files. The returned factory can be used by client
   * applications that wish to process standalone configuration files with the
   * <a href="http://www.sapia-oss.org/projects/confix">Confix </a> API.
   * 
   * @return an <code>ObjectFactoryIF</code>.
   */
  public ObjectFactoryIF getObjectFactory();
  
  /**
   * Returns the object representation of the given resource, using the 
   *  <a href="http://www.sapia-oss.org/projects/confix">Confix </a> API.
   */
  public Object include(String uri, Map params) throws ConfigurationException;
  
  /**
   * Returns the object representation of the given resource, using the 
   *  <a href="http://www.sapia-oss.org/projects/confix">Confix </a> API.
   */
  public Object include(String uri, TemplateContextIF params) throws ConfigurationException;
    
  
  /**
   * @see SotoContainer#bind(java.lang.String, java.lang.Object)
   */
  public void bind(String id, Object o);
  
  /**
   * @see SotoContainer#resolveRef(java.lang.String)
   */
  public Object resolveRef(String id) throws NotFoundException;
  
  /**
   * @return this instance's <code>Settings</code>.
   */
  public Settings getSettings();
  
}
