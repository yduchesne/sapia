package org.sapia.soto.state.cocoon.simple;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.sapia.soto.Env;
import org.sapia.soto.state.cocoon.CocoonContext;
import org.xml.sax.ContentHandler;

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
public class SimpleContext extends CocoonContext {

  /**
   * @param env
   * @param handler
   * @param objectModel
   */
  public SimpleContext(Env env, ContentHandler handler, Map objectModel) {
    super(env, handler, objectModel);
  }

  /**
   * @see org.sapia.soto.state.cocoon.CocoonContext#getInputStream()
   */
  public InputStream getInputStream() throws IOException {
    return ((SimpleRequest) super.getRequest()).getInternalRequest()
        .getInputStream();
  }

  /**
   * @see org.sapia.soto.state.cocoon.CocoonContext#getOutputStream()
   */
  public OutputStream getOutputStream() throws IOException {
    return ((SimpleResponse) super.getResponse()).getInternalResponse()
        .getOutputStream();
  }

}
