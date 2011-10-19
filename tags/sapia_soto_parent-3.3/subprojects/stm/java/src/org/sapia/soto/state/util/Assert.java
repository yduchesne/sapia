package org.sapia.soto.state.util;

import org.apache.commons.lang.StringUtils;

import org.sapia.soto.state.Result;
import org.sapia.soto.state.helpers.StepSupport;

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
public class Assert extends StepSupport {
  private static final String DEFAULT_MSG = "Value not found for: ";
  private String              _key;
  private String[]            _scopes;
  private String              _msg;

  public Assert() {
  }

  /**
   * @see org.sapia.soto.state.Executable#execute(org.sapia.soto.state.Result)
   */
  public void execute(Result res) {
    Object val = null;

    if(_scopes == null) {
      val = res.getContext().get(_key);
    } else {
      val = res.getContext().get(_key, _scopes);
    }

    if(val == null) {
      if(_msg == null) {
        res.error(DEFAULT_MSG + _key);
      } else {
        res.error(_msg);
      }
    }
  }

  public void setKey(String key) {
    _key = key;
  }

  public void setMsg(String msg) {
    _msg = msg;
  }

  public void setScopes(String scopes) {
    _scopes = StringUtils.split(scopes, ",");

    for(int i = 0; i < _scopes.length; i++) {
      _scopes[i] = _scopes[i].trim();
    }
  }
}
