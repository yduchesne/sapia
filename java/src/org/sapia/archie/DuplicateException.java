package org.sapia.archie;


/**
 * Thrown when a duplication is detected (for example, when a node with a given name
 * already exits for a new node that uses the same name).
 * 
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class DuplicateException extends ProcessingException {
  /**
   * Constructor for DuplicateException.
   */
  public DuplicateException(String msg) {
    super(msg);
  }
}
