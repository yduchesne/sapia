package org.sapia.util.license.test;

import java.io.IOException;

import org.sapia.util.license.License;

/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class TestLicense implements License{
  
  public boolean valid;
  
  /**
   * @see org.sapia.util.license.License#activate(java.lang.Object)
   */
  public void activate(Object context) {
   
  }
  
  public boolean isValid(Object context) {
    return valid;
  }
  
  /**
   * @see org.sapia.util.license.License#getBytes()
   */
  public byte[] getBytes() throws IOException {
    return Boolean.toString(valid).getBytes();
  }

}
