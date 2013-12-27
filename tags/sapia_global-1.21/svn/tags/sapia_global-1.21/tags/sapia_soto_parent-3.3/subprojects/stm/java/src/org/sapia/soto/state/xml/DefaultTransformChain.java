package org.sapia.soto.state.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

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
public class DefaultTransformChain implements TransformChain {

  private List _handlers = new ArrayList(5);

  /**
   * @see org.sapia.soto.state.xml.TransformChain#add(javax.xml.transform.sax.TransformerHandler)
   */
  public void add(TransformerHandler handler) {
    _handlers.add(handler);
  }

  /**
   * @see org.sapia.soto.state.xml.TransformChain#getTransformerHandlers()
   */
  public List getTransformerHandlers() {
    return _handlers;
  }

}
