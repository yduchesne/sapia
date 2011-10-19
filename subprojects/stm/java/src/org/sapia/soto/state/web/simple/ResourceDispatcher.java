package org.sapia.soto.state.web.simple;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.sapia.soto.state.Context;
import org.sapia.soto.state.Output;
import org.sapia.soto.state.dispatcher.Dispatcher;

import simple.http.serve.FileContext;

/**
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
public class ResourceDispatcher implements Dispatcher {

  private FileContext _context  = new FileContext();
  private List        _matchers = new ArrayList();

  public void setRoot(String root) {
    _context = new FileContext(new File(root));
  }

  public ResourceMatcherPool createMatch() {
    ResourceMatcherPool pool = new ResourceMatcherPool();
    _matchers.add(pool);
    return pool;
  }

  /**
   * @see org.sapia.soto.state.dispatcher.Dispatcher#dispatch(java.lang.String,
   *      org.sapia.soto.state.Context)
   */
  public boolean dispatch(String path, Context ctx) throws Exception {
    ResourceMatcherPool pool;
    Output out = (Output) ctx;
    for(int i = 0; i < _matchers.size(); i++) {
      pool = (ResourceMatcherPool) _matchers.get(i);
      if(pool.matches(path, out.getOutputStream(), _context)) {
        return true;
      }
    }
    return false;
  }

}
