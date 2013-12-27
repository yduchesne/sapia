package org.sapia.soto.state.xml;

import java.util.List;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.TransformerHandler;

import org.sapia.soto.Debug;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.SotoContainer;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.StepState;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

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
public class TransformState extends StepState implements EnvAware {

  private Env _env;

  /**
   * @see org.sapia.soto.EnvAware#setEnv(org.sapia.soto.Env)
   */
  public void setEnv(Env env) {
    _env = env;
  }

  /**
   * @see org.sapia.soto.state.ExecContainer#execute(org.sapia.soto.state.Result)
   */
  public void execute(Result res) {
    TransformChain chain = new DefaultTransformChain();
    XMLContext ctx;
    try {
      ctx = (XMLContext) res.getContext();
    } catch(ClassCastException e) {
      throw new IllegalStateException("Expected context to be instance of "
          + XMLContext.class.getName() + "; got: "
          + res.getContext().getClass().getName());
    }

    if(ctx.getContentHandler() == null) {
      return;
    }

    ctx.getViewParams().put(SotoContainer.SOTO_ENV_KEY, _env);

    ctx.push(chain);
    super.execute(res);
    if(res.isAborted() || res.isError()) {
      ctx.pop();
      return;
    }
    ctx.pop();
    List handlers = chain.getTransformerHandlers();

    if(handlers.size() == 0) {
      if(Debug.DEBUG){
        Debug.debug(getClass(), "No chained handlers");
      }
      process(res, ctx.getContentHandler());
    } else if(handlers.size() == 1) {
      if(Debug.DEBUG){
        Debug.debug(getClass(), "Single chained handler");
      }      
      TransformerHandler handler = (TransformerHandler) handlers.get(0);
      handler.setResult(new SAXResult(ctx.getContentHandler()));
      process(res, handler);
    } else {
      if(Debug.DEBUG){
        Debug.debug(getClass(), "Multiple chained handlers");
      }      
      TransformerHandler first = null;
      TransformerHandler current;
      int size = handlers.size() - 1;
      for(int i = 0; i < size; i++) {
        current = (TransformerHandler) handlers.get(i);
        if(i == 0) {
          first = current;
        }
        if(Debug.DEBUG){
          Debug.debug(getClass(), "Chaining from: " + current + " to " + handlers.get(i + 1) );
        }              
        current.setResult(new SAXResult((TransformerHandler) handlers
            .get(i + 1)));
      }
      ((TransformerHandler) handlers.get(handlers.size() - 1))
          .setResult(new SAXResult(ctx.getContentHandler()));

      process(res, first);
    }
  }

  /**
   * This template method can be overriden by child classes. By default, it
   * expects an <code>InputSource</code> to be on the context stack. That
   * <code>InputSource</code> is used to feed the transform chain.
   * 
   * @param res
   * @param handler
   */
  protected void process(Result res, ContentHandler handler) {
    SAXParserFactory fac = SAXParserFactory.newInstance();
    fac.setNamespaceAware(true);
    fac.setValidating(false);
    if(!res.getContext().hasCurrentObject()) {
      res.error("No object on context stack;" + InputSource.class.getName()
          + " expected");
    }
    try {
      XMLReader reader = fac.newSAXParser().getXMLReader();
      reader.setContentHandler(handler);
      reader.parse((InputSource) res.getContext().currentObject());
    } catch(ClassCastException e) {
      res.error(InputSource.class.getName() + " expected on stack; got: "
          + res.getContext().currentObject());
    } catch(Exception e) {
      res.error("Could not parse input source", e);
    }
  }

  protected Env env() {
    return _env;
  }

}
