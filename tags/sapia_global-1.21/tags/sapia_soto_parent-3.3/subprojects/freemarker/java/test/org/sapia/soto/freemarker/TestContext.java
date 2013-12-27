package org.sapia.soto.freemarker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.sapia.soto.state.ContextImpl;
import org.sapia.soto.state.MVC;
import org.sapia.soto.state.Output;

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
public class TestContext extends ContextImpl implements MVC, Output {

  private OutputStream _bos  = new ByteArrayOutputStream();

  private Map          _view = new HashMap();

  /**
   * @see org.sapia.soto.state.Output#getOutputStream()
   */
  public OutputStream getOutputStream() throws IOException {
    return _bos;
  }

  /**
   * @see org.sapia.soto.state.Output#setOutputStream(java.io.OutputStream)
   */
  public void setOutputStream(OutputStream os) throws IOException {
    _bos = os;
  }

  /**
   * @see org.sapia.soto.state.MVC#getViewParams()
   */
  public Map getViewParams() {
    return _view;
  }

}
