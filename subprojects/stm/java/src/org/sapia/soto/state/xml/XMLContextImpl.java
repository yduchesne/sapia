package org.sapia.soto.state.xml;

import java.util.Map;

import org.sapia.soto.state.ContextImpl;
import org.sapia.soto.state.MapScope;
import org.xml.sax.ContentHandler;

/**
 * An implementation of the <code>XMLContext</code> interface.
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
public class XMLContextImpl extends ContextImpl implements XMLContext {

  public static final String SCOPE_VIEW = "view";

  private MapScope           _viewParams;
  private ContentHandler     _handler;

  public XMLContextImpl() {
    _viewParams = new MapScope();
    super.addScope(SCOPE_VIEW, _viewParams);
  }

  /**
   * @see org.sapia.soto.state.xml.XMLContext#getViewParams()
   */
  public Map getViewParams() {
    return _viewParams;
  }

  /**
   * @see org.sapia.soto.state.xml.XMLContext#setContentHandler(org.xml.sax.ContentHandler)
   */
  public void setContentHandler(ContentHandler handler) {
    _handler = handler;
  }

  /**
   * @see org.sapia.soto.state.xml.XMLContext#getContentHandler()
   */
  public ContentHandler getContentHandler() {
    return _handler;
  }

}
