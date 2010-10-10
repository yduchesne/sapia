package org.sapia.soto.state.config;

import org.sapia.soto.util.matcher.PathPattern;

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
public class StatePattern {
  private PathPattern _pattern;

  public StatePattern() {
  }

  public void setId(String pattern) {
    setPattern(pattern);
  }

  public void setPattern(String pattern) {
    _pattern = PathPattern.parse(pattern, true);
  }

  public boolean matches(String stateId) {
    return _pattern.matches(stateId);
  }
}
