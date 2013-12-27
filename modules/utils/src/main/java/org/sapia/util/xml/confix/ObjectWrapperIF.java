package org.sapia.util.xml.confix;


/**
 * An instance of this interface wrap an object that eventually "receives" method
 * calls from <code>ConfixProcessorIF</code> instances.
 *
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface ObjectWrapperIF {
  /**
   * Returns the object encapsulated by this wrapper.
   *
   * @return The object encapsulated by this wrapper.
   */
  public Object getWrappedObject();
}
