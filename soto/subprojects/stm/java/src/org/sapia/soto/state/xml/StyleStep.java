package org.sapia.soto.state.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.ClassUtils;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.config.SotoIncludeContext;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.Step;
import org.sapia.resource.Resource;
import org.sapia.resource.ResourceHandler;
import org.sapia.soto.util.Utils;
import org.sapia.util.text.TemplateContextIF;

/**
 * An instance of this class encapsulates the behavior necessary to create
 * <code>TransformerHandler</code> s from XSL stylesheets. The instance is
 * given the source to a stylesheet; whenever possible, it detects if the
 * stylesheet has been modified and reloads it - currently, such a feature is
 * available only for URIs that correspond to a <code>java.io.File</code>
 * object.
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
public class StyleStep implements EnvAware, Step {
  private Templates             _templ;
  private SAXTransformerFactory _fac;
  private Resource              _file;
  private Env                   _env;
  private long                  _lastModified = System.currentTimeMillis();

  public StyleStep() throws TransformerConfigurationException {
    _fac = (SAXTransformerFactory) TransformerFactory.newInstance();
  }

  protected StyleStep(SAXTransformerFactory fac) {
    _fac = fac;
  }

  /**
   * @see org.sapia.soto.state.Step#getName()
   */
  public String getName() {
    return ClassUtils.getShortClassName(getClass());
  }

  /**
   * @param uri
   *          the URI to an XSL stylesheet.
   * @throws IOException
   * @throws TransformerConfigurationException
   */
  public void setSrc(String uri) throws IOException,
      TransformerConfigurationException {
    _fac.setURIResolver(new URIResolverImpl(_env));
    ResourceHandler handler = (ResourceHandler) _env.getResourceHandlerFor(uri);
    _file = handler.getResourceObject(uri);
    _templ = _fac.newTemplates(new StreamSource(process(_file.getInputStream(),
          uri)));
  }

  /**
   * @see org.sapia.soto.state.Executable#execute(org.sapia.soto.state.Result)
   */
  public void execute(Result res) {
    XMLContext ctx;
    try {
      ctx = (XMLContext) res.getContext();
    } catch(ClassCastException e) {
      throw new IllegalStateException("Expected context to be instance of "
          + XMLContext.class.getName() + "; got: "
          + res.getContext().getClass().getName());
    }

    Map params = ctx.getViewParams();

    if((_file != null) && (_file.lastModified() != _lastModified)) {
      try {
        reload();
      } catch(Exception e) {
        res.error("Problem reloading stylesheet", e);
        return;
      }
    }

    TransformerHandler handler;
    try {
      handler = _fac.newTransformerHandler(_templ);
    } catch(TransformerConfigurationException e) {
      res.error("Problem creating TransformerHandler", e);
      return;
    }
    Object key;
    Object val;

    for(Iterator iter = params.keySet().iterator(); iter.hasNext();) {
      key = iter.next();
      val = params.get(key);

      if(val != null) {
        handler.getTransformer().setParameter(key.toString(), val);
      }
    }
    try {
      TransformChain chain = (TransformChain) ctx.currentObject();
      chain.add(handler);
    } catch(ClassCastException e) {
      throw new IllegalStateException(
          "Expected object on context stack to be instance of "
              + TransformChain.class.getName());
    }
  }

  /**
   * @see org.sapia.soto.EnvAware#setEnv(org.sapia.soto.Env)
   */
  public void setEnv(Env env) {
    _env = env;
  }

  private synchronized void reload() throws TransformerConfigurationException,
      IOException {
    if(_file.lastModified() != _lastModified) {
      _templ = _fac.newTemplates(new StreamSource(_file.getInputStream(), _file
          .getURI()));
    }

    _lastModified = _file.lastModified();
  }

  private InputStream process(InputStream is, String uri) throws IOException {
    TemplateContextIF ctx = SotoIncludeContext.currentTemplateContext();

    return Utils.replaceVars(ctx, is, uri);
  }

}
