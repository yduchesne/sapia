package org.sapia.util.license;

import java.io.IOException;
import java.io.Serializable;

/**
 * This interface specifies the basic behavior of all licenses.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface License extends Serializable{
  
  /**
   * Returns if this license is valid or not. This method takes an
   * arbitratry context object that can be passed in by client applications.
   * This has been thought in cases where license validity may depend on 
   * external parameters (max connected users, etc.).
   * <p>
   * This requires client apps to know about the concrete type of licenses
   * they deal with.
   * 
   * @return <code>true</code> if this license is valid.
   * @param an arbitraty context <code>Object</code>.
   */
  public boolean isValid(Object context);
  
  /**
   * This method is called by application code on the client-side
   * to activate this license. This method should only be called once in
   * a given license's lifecycle.
   * <p>
   * An object holding arbitrary contextual data can be passed in. This
   * requires client apps to know abou the concrete type of licenses
   * that deal with.
   * 
   * @param an arbitraty context <code>Object</code>.
   */
  public void activate(Object context);
  
  /**
   * @return the byte representation of this license; these bytes will
   * be signed, so the representation must be constant.
   * 
   * @throws IOException if a problem occurs generating the bytes for
   * this instance.
   */
  public byte[] getBytes() throws IOException;

}
