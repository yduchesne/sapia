package org.sapia.corus.interop;

/**
 * This class represents the <code>Poll</code> Corus interop request. This request is generated
 * by a process managed by a Corus server and it act as a heartbeat sent to the Corus
 * server.
 *
 * @author <a href="mailto:jc@sapia-oss.org">Jean-Cedric Desrochers</a>
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">
 *     Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *     <a href="http://www.sapia-oss.org/license.html" target="sapia-license">license page</a>
 *     at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class Poll extends AbstractCommand {
  
  static final long serialVersionUID = 1L;

  /**
   * Creates a new Poll instance.
   */
  public Poll() {
  }
}
