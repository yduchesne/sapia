package org.sapia.soto.state.cocoon.simple;

import java.io.OutputStream;
import java.util.LinkedList;

import simple.http.serve.Context;

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
public class ResourceMatcherPool {

  private String     _target, _pattern;
  private LinkedList _pool = new LinkedList();

  public void setTarget(String target) {
    _target = target;
  }

  public void setPattern(String pattern) {
    _pattern = pattern;
  }

  public boolean matches(String path, OutputStream io, Context ctx)
      throws Exception {
    ResourceMatcher matcher = null;
    try {
      matcher = (ResourceMatcher) acquire();
      return matcher.matches(path, io, ctx);
    } finally {
      if(matcher != null)
        release(matcher);
    }
  }

  private synchronized ResourceMatcher acquire() {
    if(_pool.size() == 0) {
      ResourceMatcher matcher = new ResourceMatcher();
      if(_pattern != null) {
        matcher.setPattern(_pattern);
      }
      if(_target != null) {
        matcher.setTarget(_target);
      }
      return matcher;
    } else {
      return (ResourceMatcher) _pool.removeFirst();
    }
  }

  private synchronized void release(ResourceMatcher matcher) {
    _pool.add(matcher);
  }

}
