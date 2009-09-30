package org.sapia.corus.admin.services.processor;

import org.sapia.corus.LogicException;


/**
 * Indicates that the lock on a given process could not be obtained.
 * 
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class LockException extends LogicException {
  LockException(String msg) {
    super(msg);
  }
}
