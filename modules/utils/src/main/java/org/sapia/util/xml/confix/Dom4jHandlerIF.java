package org.sapia.util.xml.confix;

import org.dom4j.Element;


/**
 * This interface defines the behavior that should be implemented by classes whose
 * objects "consume" XML.
 * </p>
 * This interface binds implementations to the Dom4j api; using the <code>XMLConsumer</code>
 * interface is preferrable.
 * 
 * @see org.sapia.util.xml.confix.XMLConsumer
 *
 * @author  Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface Dom4jHandlerIF {
  /**
   * Call-back that is invoked by a <code>Dom4jProcessor</code>.
   *
   * @param elem a Dom4j <code>Element</code>.
   */
  public void handleElement(Element elem) throws ConfigurationException;
}
