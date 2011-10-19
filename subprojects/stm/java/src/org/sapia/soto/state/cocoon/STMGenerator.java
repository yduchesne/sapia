package org.sapia.soto.state.cocoon;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.logger.Logger;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.generation.ServiceableGenerator;
import org.apache.excalibur.source.SourceResolver;
import org.sapia.resource.Resource;
import org.sapia.resource.ResourceHandler;
import org.sapia.resource.UrlResource;
import org.sapia.soto.util.Utils;
import org.xml.sax.SAXException;

/**
 * This class is meant to integrate Soto's STM API with Cocoon. An instance 
 * of this class delegates its work to a dispatcher.
 * 
 * @see org.sapia.soto.state.dispatcher.Dispatcher
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
public class STMGenerator extends ServiceableGenerator implements Composable {
  static final String ROLE = "role";
  protected Logger           _log;
  protected ComponentManager _manager;

  /**
   * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(Logger)
   */
  public void enableLogging(Logger logger) {
    _log = logger.getChildLogger(this.getClass().getName());
  }

  /**
   * @see org.apache.avalon.framework.component.Composable#compose(ComponentManager)
   */
  public void compose(ComponentManager manager) throws ComponentException {
    _manager = manager;
  }

  /**
   * @see org.apache.cocoon.generation.Generator#generate()
   */
  public void generate() throws IOException, SAXException, ProcessingException {
    
 
    try {
      CocoonDispatcher dispatcher = (CocoonDispatcher)_manager.lookup(parameters.getParameter(ROLE, CocoonDispatcher.ROLE));
      Request req = ObjectModelHelper.getRequest(objectModel);
      String uri = req.getRequestURI();
      if(_log.isDebugEnabled()){
        _log.debug("Dispatching URI in " + getClass().getName() + ": " + uri);
      }
      if(!dispatcher.dispatch(uri, contentHandler, objectModel)){
        throw new ProcessingException("Unknown URI: " + uri);
      }
    } catch(Exception e) {
      if(e instanceof ProcessingException){
        throw (ProcessingException)e;
      }
      throw new ProcessingException("Could not execute state", e);
    }
  }
  
 
  protected Logger logger() {
    return _log;
  }

  static final class CocoonResourceHandler implements ResourceHandler {

    public static final String      CONTEXT_SCHEME = "context";
    public static final String      COCOON_SCHEME  = "cocoon";
    STMGenerator _owner;

    CocoonResourceHandler(STMGenerator owner) {
      _owner = owner;
    }

    /**
     * @see org.sapia.soto.util.ResourceHandler#getResource(java.lang.String)
     */
    public InputStream getResource(String uri) throws IOException {
      return getResourceObject(uri).getInputStream();
    }

    /**
     * @see org.sapia.soto.util.ResourceHandler#getResourceObject(java.lang.String)
     */
    public Resource getResourceObject(String uri) throws IOException {
      SourceResolver res = null;

      try {
        res = (SourceResolver) _owner._manager.lookup(SourceResolver.ROLE);
        String toResolve = Utils.chopScheme(uri);
        toResolve = CONTEXT_SCHEME + "://" + Utils.chopScheme(uri);

        return new UrlResource(new URL(toResolve));
      } catch(ComponentException e) {
        throw new IOException(e.getMessage());
      } finally {
        if(res != null) {
          _owner._manager.release(res);
        }
      }
    }

    /**
     * @see org.sapia.soto.util.ResourceHandler#accepts(java.lang.String)
     */
    public boolean accepts(String uri) {
      String scheme = Utils.getScheme(uri);
      if(scheme == null || scheme.length() == 0) {
        return true;
      } else if(scheme.equals(COCOON_SCHEME)) {
        return true;
      } else {
        return false;
      }
    }
    
    public boolean accepts(URI uri) {
      String scheme = Utils.getScheme(uri.toString());
      if(scheme == null || scheme.length() == 0) {
        return true;
      } else if(scheme.equals(COCOON_SCHEME)) {
        return true;
      } else {
        return false;
      }
    }

  }
}
