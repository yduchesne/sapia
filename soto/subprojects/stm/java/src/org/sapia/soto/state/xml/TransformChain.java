package org.sapia.soto.state.xml;

import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

/**
 * Instances of this interface are meant to hold a
 * <code>TransformerHandler</code> list; the handler will be chained in an XML
 * pipeline.
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
public interface TransformChain {

  /**
   * @param handler
   *          a <code>TransformerHandler</code>.
   */
  public void add(TransformerHandler handler);

  /**
   * @return the <code>List</code> of <code>TransformerHandler</code> s that
   *         this instance holds.
   */
  public List getTransformerHandlers();
}
