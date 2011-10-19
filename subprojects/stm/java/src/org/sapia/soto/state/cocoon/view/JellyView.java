package org.sapia.soto.state.cocoon.view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerConfigurationException;

import org.apache.commons.jelly.Jelly;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.lang.ClassUtils;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.StateRuntimeException;
import org.sapia.soto.state.Step;
import org.sapia.soto.state.cocoon.CocoonContext;
import org.sapia.soto.state.xml.StyleStep;
import org.sapia.soto.state.xml.TransformState;
import org.sapia.soto.state.xml.XMLContext;
import org.sapia.soto.util.Param;
import org.sapia.resource.Resource;
import org.sapia.resource.ResourceHandler;
import org.sapia.soto.util.Utils;
import org.sapia.util.xml.confix.ConfigurationException;
import org.xml.sax.ContentHandler;

/**
 * An instance of this class implements XSL pipelining in the following way:
 * 
 * <ul>
 * <li>It expects the URI to a valid Jelly script to be given to it.
 * <li>If stylesheets (<code>XslSource</code> instances) are assigned to it,
 * they take part in the pipeline of SAX events that originate from the Jelly
 * script's execution.
 * </ul>
 * <p>
 * A Jelly view expects the model (in an MVC sense) to be the "current object"
 * on the execution context's stack. The current object is exported to the Jelly
 * script's context, under the name "Model", or under the name specified by the
 * application. The view parameters are also exported to the Jelly context.
 * <p>
 * An instance of this class checks if its Jelly script source has been
 * modified, and reloads it if such is the case. If the given script URI does
 * not correspond to a <code>File</code> object, then there is no way to check
 * for such modifications, and in such cases such a check is impossible. To make
 * sure that this feature is "on", use the <code>file</code> protocol when
 * setting URIs (e.g.: file:/some/jellyScript.xml).
 * <p>
 * See <a href="http://jakarta.apache.org/jelly">Jelly's doc </a> for more info.
 * 
 * @see org.sapia.soto.state.Context
 * @see org.sapia.soto.state.cocoon.CocoonContext#getViewParams()
 * 
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
public class JellyView extends TransformState implements Step {
  private Script   _jelly;
  private String   _uri;
  private Resource _res;
  private long     _lastModified;
  private List     _params = new ArrayList();

  /**
   * @see org.sapia.soto.state.Step#getName()
   */
  public String getName() {
    return ClassUtils.getShortClassName(getClass());
  }

  /**
   * @param uri
   *          the URI to a Jelly script.
   * @throws IOException
   * @throws Exception
   */
  public void setSrc(String uri) throws IOException, Exception {
    _uri = uri;

    ResourceHandler handler = (ResourceHandler) env().getResourceHandlerFor(
        Utils.chopScheme(uri));
    _res = handler.getResourceObject(uri);

    Jelly j = new Jelly();
    _lastModified = _res.lastModified();
    j.setUrl(new URL(_res.getURI()));
    _jelly = j.compileScript();
  }

  /**
   * Internally creates and adds a parameter to this instance, and returns that
   * parameter.
   * 
   * @return a <code>Param</code>
   */
  public Param createParam() {
    Param p = new Param();
    _params.add(p);
    return p;
  }

  /**
   * @see org.sapia.soto.state.xml.TransformState#process(org.sapia.soto.state.Result,
   *      org.xml.sax.ContentHandler)
   */
  protected void process(Result res, ContentHandler handler) {
    XMLContext ctx = (XMLContext) res.getContext();

    Object model = null;

    if(ctx.hasCurrentObject()) {
      model = ctx.currentObject();
    }

    try {
      execute(model, ctx.getViewParams(), handler);
    } catch(Throwable t) {
      throw new StateRuntimeException("Could not run Jelly state: " + getId()
          + " for script: " + _uri, t);
    }
  }

  protected void execute(Object model, Map viewParams, ContentHandler handler)
      throws Throwable {
    if(_jelly == null) {
      throw new IllegalStateException("Jelly script not set on Jelly view");
    }

    if(_lastModified != _res.lastModified()) {
      reload();
    }

    JellyContext jelly = new JellyContext();
    JellyUtils.copyParamsTo(jelly, viewParams);
    jelly.setCurrentURL(new URL(_res.getURI()));
    jelly.setVariable(CocoonContext.MODEL_KEY, model);

    Param p;
    for(int i = 0; i < _params.size(); i++) {
      p = (Param) _params.get(i);
      if(p.getName() != null && p.getValue() != null) {
        jelly.setVariable(p.getName(), p.getValue());
      }
    }

    XMLOutput output = new XMLOutput(handler);
    output.startDocument();
    _jelly.run(jelly, output);
    output.endDocument();
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
        super.handleObject(name, obj);
      } catch(TransformerConfigurationException e) {
        throw new ConfigurationException("Could not instantiate StyleStep", e);
      } catch(IOException e) {
        throw new ConfigurationException("Could not inialize stylesheet", e);
      }
    } else {
      super.handleObject(name, obj);
    }
  }

  private synchronized void reload() throws Exception {
    if(_res.lastModified() != _lastModified) {
      Jelly j = new Jelly();
      j.setUrl(new URL(_res.getURI()));
      _jelly = j.compileScript();
    }

    _lastModified = _res.lastModified();
  }
}
