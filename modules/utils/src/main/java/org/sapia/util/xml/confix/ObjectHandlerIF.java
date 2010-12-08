package org.sapia.util.xml.confix;


/**
 * An instance of this interface handles objects that corresponds to given XML
 * elements.
 *
 * @author JC Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface ObjectHandlerIF {
  /**
   * Handles the passed in object that was created for the element name passed in.
   *
   * @param anElementName The xml element name for which the object was created.
   * @param anObject The object to handle.
   *
   * @throws ConfigurationException if this instance does not "know" (cannot handle) the
   * passed in object/element name.
   */
  public void handleObject(String anElementName, Object anObject)
    throws ConfigurationException;
}
