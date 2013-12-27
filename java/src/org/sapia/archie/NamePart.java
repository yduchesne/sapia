package org.sapia.archie;


/**
 * Models a part in a <code>Name</code>.
 * 
 * @see org.sapia.archie.Name
 * @see org.sapia.archie.NameParser
 * 
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface NamePart extends java.io.Serializable{
  
  /**
   * @return this instance as a string that can be parsed by the appropriate
   * <code>NameParser</code>.
   * 
   * @see NameParser
   */
  public String asString();
}
