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
public class SecondaryService implements Service {
  private MasterService _svc;

  /**
   * Constructor for SecondaryService.
   */
  public SecondaryService() {
    super();
  }

  public void setSomeService(MasterService svc) {
    _svc = svc;
  }

  public MasterService getSomeService() {
    return _svc;
  }

  public boolean hasMaster() {
    return _svc != null;
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

  public void doSomethingElse() {
    System.out.println("Calling master service...");
    _svc.doSomething();
  }
}
