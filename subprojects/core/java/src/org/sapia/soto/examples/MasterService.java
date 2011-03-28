package org.sapia.soto.examples;

import org.sapia.soto.Service;

/**
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2003 <a
 *         href="http://www.sapia-oss.org">Sapia Open Source Software </a>. All
 *         Rights Reserved.</dd>
 *         </dt>
 *         <dt><b>License: </b>
 *         <dd>Read the license.txt file of the jar or visit the <a
 *         href="http://www.sapia-oss.org/license.html">license page </a> at the
 *         Sapia OSS web site</dd>
 *         </dt>
 *         </dl>
 */
public class MasterService implements Service {
  private String _msg;

  /**
   * Constructor for MasterService.
   */
  public MasterService() {
    super();
  }

  /**
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
    System.out.println("initializing " + getClass());
  }

  /**
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {
    System.out.println("starting " + getClass());
  }

  /**
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
  }

  public void setMessage(String msg) {
    _msg = msg;
  }

  public String getMessage() {
    return _msg;
  }

  public void doSomething() {
    System.out.println("This is a message: " + _msg);
  }
}
