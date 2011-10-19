package org.sapia.soto.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sapia.soto.Debug;
import org.sapia.soto.Env;
import org.sapia.soto.SotoApplicationFactory;
import org.sapia.soto.util.Param;
import org.sapia.resource.Resource;
import org.sapia.util.text.MapContext;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
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
public class IncludeTag implements ObjectCreationCallback {
  private String                 _uri;
  private Resource               _resource;
  //private SotoContainer          _container;
  private Env                    _env;
  private SotoApplicationFactory _fac;
  private List                   _params   = new ArrayList();

  /**
   * Constructor for Include.
   */
  public IncludeTag(Env env, SotoApplicationFactory fac) {
    _env = env;
    _fac = fac;
  }

  public void setUri(String uri) {
    _uri = uri;
  }

  public String getUri() {
    return _uri;
  }

  public Param createParam() {
    Param p = new Param();
    _params.add(p);

    return p;
  }
  
  public Resource getResource(){
    return _resource;
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    return include();
  }
  
  /**
   * @return the <code>Object</code> corresponding to the included resource.
   * @throws ConfigurationException
   */
  public Object include() throws ConfigurationException{
    if(_uri == null) {
      throw new ConfigurationException(
          "'uri' attribute not set on 'include' element");
    }

    if (Debug.DEBUG) {
      Debug.debug("<soto:include> including resource '" + _uri + "'");
    }
    
    return _env.include(_uri, getNestedContext());
    
    /*TemplateContextIF ctx = getNestedContext();
    Include incl = ThreadState.include(_env, _uri, ctx);
    Object toReturn = incl.performInclude();
    _resource = incl.getResource();
    return toReturn;*/
  }
  
  private TemplateContextIF getNestedContext() {
    Map params = new HashMap();
    Param p;

    for(int i = 0; i < _params.size(); i++) {
      p = (Param) _params.get(i);

      if(p.getName() != null) {
        params.put(p.getName(), p.getValue());
      }
    }

    SotoIncludeContext ctx = SotoIncludeContext.currentContext();
    MapContext nested;     
    if(ctx == null || ctx.getTemplateContext() == null){
      nested = new MapContext(params, _fac.getTemplateContext(), false);  
    }
    else{
      nested = new MapContext(params, ctx.getTemplateContext(), false);
    }
    return nested;
  }
}
