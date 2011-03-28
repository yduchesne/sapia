package org.sapia.soto.state.util;

import org.apache.commons.lang.ClassUtils;

import org.sapia.soto.state.Result;
import org.sapia.soto.state.Step;

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
public class Push implements Step {
  private String _from;
  private String _key;

  /**
   * @see org.sapia.soto.state.Step#getName()
   */
  public String getName() {
    return ClassUtils.getShortClassName(getClass());
  }

  public void setFrom(String from) {
    _from = from;
  }

  public void setKey(String key) {
    _key = key;
  }

  /**
   * @see org.sapia.soto.state.Executable#execute(org.sapia.soto.state.Result)
   */
  public void execute(Result res) {
    if(_key == null) {
      throw new IllegalStateException("'key' not specified");
    }

    Object toExport;

    if(_from != null) {
      toExport = res.getContext().get(_key, _from);
    } else {
      toExport = res.getContext().get(_key);
    }

    if(toExport != null) {
      res.getContext().push(toExport);
    }
  }
}
