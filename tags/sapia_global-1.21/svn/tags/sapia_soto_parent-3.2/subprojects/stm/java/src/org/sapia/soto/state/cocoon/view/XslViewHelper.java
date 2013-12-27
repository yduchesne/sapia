package org.sapia.soto.state.cocoon.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;

import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.util.URIResolverImpl;
import org.xml.sax.ContentHandler;

/**
 * Helper class that can be extended by <code>State</code> implementations
 * that whish to provide XSL-based pipelining behavior. The class implements the
 * getId() and setId() methods that are eventually required by all
 * <code>state</code> implementations.
 * <p>
 * An instance of this class encapsulates <code>XslSource</code> instances
 * that act together in a pipeline of SAX events.
 * 
 * @deprecated replaced by the <code>StyleStep</code> class.
 * 
 * @see org.sapia.soto.state.xml.StyleStep
 * @see org.sapia.soto.state.cocoon.view.DomifyView
 * @see org.sapia.soto.state.cocoon.view.JellyView
 * 
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
public class XslViewHelper implements EnvAware {
  protected List                _sources = new ArrayList();
  private SAXTransformerFactory _fac;

  public XslViewHelper(SAXTransformerFactory fac) {
    _fac = fac;
  }

  /**
   * @see org.sapia.soto.EnvAware#setEnv(org.sapia.soto.Env)
   */
  public void setEnv(Env env) {
    if(!(_fac.getURIResolver() instanceof URIResolverImpl)) {
      _fac.setURIResolver(new URIResolverImpl(env, _fac.getURIResolver()));
    }
  }

  /**
   * @param src
   *          a <code>XslSource</code>.
   */
  protected void addSource(XslSource src) {
    _sources.add(src);
    src.setSaxTransformerFactory(_fac);
  }

  /**
   * @param params
   *          the <code>Map</code> that contains the view parameters.
   * @param handler
   *          the <code>ContentHandler</code> to which SAX events are to be
   *          ultimately pipelined.
   * 
   * @return a <code>TransformInfo</code>.
   * @throws TransformerConfigurationException
   * @throws TransformerException
   * @throws IOException
   */
  protected TransformInfo process(Map params, ContentHandler handler)
      throws TransformerConfigurationException, TransformerException,
      IOException {
    TransformerHandler[] handlers = new TransformerHandler[_sources.size()];
    SAXResult first = null;
    XslSource src;

    if(_sources.size() == 1) {
      src = (XslSource) _sources.get(0);
      handlers[0] = src.getTransformerHandler(params);
      handlers[0].setResult(first = new SAXResult(handler));
    } else {
      for(int i = 0; i < _sources.size(); i++) {
        src = (XslSource) _sources.get(i);
        handlers[i] = src.getTransformerHandler(params);

        if(i > 0) {
          if((i - 1) == 0) {
            handlers[i - 1].setResult(first = new SAXResult(handlers[i]));
          } else {
            handlers[i - 1].setResult(new SAXResult(handlers[i]));
          }
        }
      }

      handlers[handlers.length - 1].setResult(new SAXResult(handler));
    }

    return new TransformInfo(first, handlers[0]);
  }
}
