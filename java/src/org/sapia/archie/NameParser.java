package org.sapia.archie;


/**
 * An instance of this interface creates <code>Name</code> instances from
 * string representations.
 * <p>
 * Understood name representations are implementation specific.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface NameParser extends java.io.Serializable{
  public Name parse(String name) throws ProcessingException;
  public String asString(Name name);
  public NamePart parseNamePart(String namePart) throws ProcessingException;
}
