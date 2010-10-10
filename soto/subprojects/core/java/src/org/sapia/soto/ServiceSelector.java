package org.sapia.soto;

/**
 * Instances of this interface are used to determine if a given service is to be
 * included in the result of a lookup.
 * 
 * @see SotoContainer#lookup(ServiceSelector, boolean)
 * 
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public interface ServiceSelector {

  /**
   * @param meta
   *          a <code>ServiceMetadata</code>.
   * @return <code>true</code> if this instance determines that the given
   *         instance should be returned in the lookup result.
   */
  public boolean accepts(ServiceMetaData meta);

}
