package org.sapia.soto.state.dispatcher;

import java.util.ArrayList;
import java.util.List;

import org.sapia.soto.state.Context;

/**
 * This class implements the <code>Dispatcher</code> interface. <p/>An
 * instance of this class internally uses <code>Matcher</code> instances to
 * match URIs to states. The nature of the URIs is environment-specific (for
 * example, it can correspond to the path of a HTTP GET). <p/>Thus, an instance
 * of this class is intented for embedding in environment-specific containers.
 * 
 * @see org.sapia.soto.state.dispatcher.Matcher
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
public class DispatcherImpl implements Dispatcher {

  private List _matchers = new ArrayList();

  /**
   * @see org.sapia.soto.state.dispatcher.Dispatcher#dispatch(java.lang.String,
   *      org.sapia.soto.state.Context)
   */
  public boolean dispatch(String uri, Context ctx) throws Exception {
    MatcherPool m;
    for(int i = 0; i < _matchers.size(); i++) {
      m = (MatcherPool) _matchers.get(i);
      if(m.matches(uri, ctx)) {
        return true;
      }
    }
    return false;
  }

  public MatcherPool createMatch() {
    MatcherPool m = new MatcherPool();
    _matchers.add(m);
    return m;
  }
}
