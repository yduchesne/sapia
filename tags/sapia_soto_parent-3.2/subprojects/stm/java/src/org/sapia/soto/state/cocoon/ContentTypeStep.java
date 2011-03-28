package org.sapia.soto.state.cocoon;

import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.Scope;
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
public class ContentTypeStep implements Step {
  public static final String DEFAULT_CONTENT_TYPE = "text/xml";
  private String             _contentType         = DEFAULT_CONTENT_TYPE;

  /**
   * @see org.sapia.soto.state.Executable#execute(org.sapia.soto.state.Result)
   */
  public void execute(Result st) {
    Map scopes = ((CocoonContext) st.getContext()).getScopes();
    Scope scope = (Scope) scopes.get(CocoonContext.HEADERS_SCOPE);
    scope.putVal("Content-Type", _contentType);
  }

  /**
   * @see org.sapia.soto.state.Step#getName()
   */
  public String getName() {
    return ClassUtils.getShortClassName(getClass());
  }

  public void setType(String ct) {
    _contentType = ct;
  }
}
