package org.sapia.soto.state.dispatcher;

import org.sapia.soto.Service;
import org.sapia.soto.state.Context;

/**
 * This class is a <code>Dispatcher</code> that implements the <code>Service</code>
 * interface.
 * 
 * @see org.sapia.soto.state.dispatcher.Dispatcher
 * @see org.sapia.soto.Service
 * 
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class DispatcherService extends DispatcherImpl implements Service{

  /**
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
  }

  /**
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
  }

  /**
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {
  }
  
  /**
   * @see org.sapia.soto.state.dispatcher.DispatcherImpl#dispatch(java.lang.String, org.sapia.soto.state.Context)
   */
  public boolean dispatch(String uri, Context ctx) throws Exception {
    return super.dispatch(uri, ctx);
  }

}
