package org.sapia.soto.state.cocoon.view;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;

import org.apache.commons.lang.ClassUtils;
import org.infohazard.domify.DOMAdapter;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.StateRuntimeException;
import org.sapia.soto.state.Step;
import org.sapia.soto.state.cocoon.CocoonContext;
import org.sapia.soto.state.xml.StyleStep;
import org.sapia.soto.state.xml.TransformState;
import org.sapia.soto.state.xml.XMLContext;
import org.sapia.util.xml.confix.ConfigurationException;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

/**
 * This class behaves similarly to the <code>JellyView</code>, excepts that
 * it wraps the model in a w3c <code>Node</code> implementation. The
 * <code>Domify</code> toolkit is used for this.
 * <p>
 * An instance of this class must be configured with at least one XSL source.
 * 
 * @see org.sapia.soto.state.cocoon.view.JellyView
 * @see org.sapia.soto.state.cocoon.view.XslSource
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
public class DomifyView extends TransformState implements Step {
  private String             _name;
  private TransformerFactory _fac;

  public DomifyView() {
    _fac = TransformerFactory.newInstance();
  }

  /**
   * @see org.sapia.soto.state.Step#getName()
   */
  public String getName() {
    return ClassUtils.getShortClassName(getClass());
  }

  /**
   * @param name
   *          the XML root element name of the XML that is to be produced as the
   *          result of the model's serialization as SAX events.
   */
  public void setRootName(String name) {
    _name = name;
  }

  /**
   * @see org.sapia.soto.state.xml.TransformState#process(org.sapia.soto.state.Result,
   *      org.xml.sax.ContentHandler)
   */
  protected void process(Result res, ContentHandler handler) {
    XMLContext ctx = (XMLContext) res.getContext();

    if(ctx.getContentHandler() == null) {
      return;
    }

    Object model = null;

    if(ctx.hasCurrentObject()) {
      model = ctx.currentObject();
    } else {
      model = new NullObject();
    }

    try {
      execute(model, ctx.getViewParams(), handler);
    } catch(Throwable t) {
      throw new StateRuntimeException("Could not execute XSL pipeline", t);
    }
  }
  
  public void setSrc(String xsl) throws Exception{
    StyleStep step = new StyleStep();
    step.setEnv(env());
    step.setSrc(xsl);
    super.addExecutable(step);
  }

  protected void execute(Object model, Map viewParams, ContentHandler handler)
      throws Throwable {

    if(_name == null) {
      _name = CocoonContext.MODEL_KEY;
    }

    DOMSource src = model instanceof Node ? 
                    new DOMSource((Node)model) :
                    new DOMSource(new DOMAdapter().adapt(model, _name));
      
    SAXResult res = new SAXResult(handler);
    Transformer tx = _fac.newTransformer();
    Iterator params = viewParams.entrySet().iterator();
    Map.Entry entry;
    while(params.hasNext()) {
      entry = (Map.Entry) params.next();
      tx.setParameter(entry.getKey().toString(), entry.getValue());
    }
    tx.transform(src, res);
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(java.lang.String,
   *      java.lang.Object)
   */
  public void handleObject(String name, Object obj)
      throws ConfigurationException {
    if(obj instanceof XslSource) {
      try {
        XslSource xsl = (XslSource) obj;
        StyleStep step = new StyleStep();
        step.setEnv(env());
        step.setSrc(xsl.getUri());
        super.handleObject(name, step);
      } catch(TransformerConfigurationException e) {
        throw new ConfigurationException("Could not instantiate StyleStep", e);
      } catch(IOException e) {
        throw new ConfigurationException("Could not inialize stylesheet", e);
      }
    } else {
      super.handleObject(name, obj);
    }
  }

  // INNER CLASSES //////////////////////////////////////////////
  public static class NullObject {
  }
}
