package org.sapia.util.license;

/**
 * Implements the <code>LicenseIdFactory</code> interface over the 
 * <code>System.currentTimeMillis()</code> method.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class SystemTimeIdFactory implements LicenseIdFactory{
  
  /**
   * @see org.sapia.util.license.LicenseIdFactory#newInstance()
   */
  public long newInstance() {
    return System.currentTimeMillis();
  }

}
