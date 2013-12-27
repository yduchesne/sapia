package org.sapia.soto.state.cocoon;

import java.io.IOException;

import org.apache.cocoon.environment.http.HttpResponse;
import org.apache.commons.lang.ClassUtils;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.Step;
import org.sapia.soto.util.Utils;

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
public class RedirectStep implements Step {
  private String _uri;

  public void setUri(String uri) {
    _uri = uri;
  }

  /**
   * @see org.sapia.soto.state.Step#getName()
   */
  public String getName() {
    return ClassUtils.getShortClassName(getClass());
  }

  /**
   * @see org.sapia.soto.state.Executable#execute(org.sapia.soto.state.Result)
   */
  public void execute(Result st) {
    CocoonContext ctx = (CocoonContext) st.getContext();
    String link;

    if(_uri == null) {
      throw new IllegalStateException("Redirect URI not specified");
    } else if(Utils.hasScheme(_uri)) {
      link = _uri;
    } else {
      link = "http://" + ctx.getRequest().getServerName() + ":"
          + ctx.getRequest().getServerPort()
          + ctx.getRequest().getContextPath() + '/' + _uri;
    }

    try {
      ((HttpResponse) ctx.getResponse()).sendRedirect(link);
    } catch(IOException e) {
      st.error(e);
    }
  }
}
