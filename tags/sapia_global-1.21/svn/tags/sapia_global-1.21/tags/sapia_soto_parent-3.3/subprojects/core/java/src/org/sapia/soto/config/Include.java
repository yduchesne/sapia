package org.sapia.soto.config;


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
public class Include{
  //private Stack                  _includes = new Stack();
  
  /*
  private SotoIncludeContext            _owner;
  private String                 _uri;
  private Resource               _resource;
  private Env                    _env;
  private TemplateContextIF      _ctx;
  private List                   _params   = new ArrayList();

  Include(SotoIncludeContext owner, Env env, String uri, TemplateContextIF ctx) {
    _owner = owner;
    _env = env;
    _uri = uri;
    _ctx = ctx;
  }

  public String getUri() {
    return _uri;
  }

  public Param createParam() {
    Param p = new Param();
    _params.add(p);

    return p;
  }
  
  public Resource getResource() {
    if(_resource == null) {
      throw new IllegalStateException("Included resource not set");
    }

    return _resource;
  }  
  
  public Object performInclude() throws ConfigurationException{
    SotoIncludeContext.push(_owner);    
    try{
      return doPerformInclude(true);
    }finally{
      SotoIncludeContext.pop();    
    }
  }
  
  public Resource performIncludeResource() throws ConfigurationException {
    SotoIncludeContext.push(_owner);
    try{
      return (Resource)doPerformInclude(false);
    }finally{
      SotoIncludeContext.pop();
    }
  }  
  
  public Object doPerformInclude(boolean process) throws ConfigurationException{
    InputStream input = null;
    try {
      if(_resource != null){
        Debug.debug(getClass(), "Including: " + _resource.getURI());
      }
      else {
        if(Debug.DEBUG)
          Debug.debug(getClass(), "Including: " + _uri);

        if(_owner.getLastInclude() == null) {
          if(Debug.DEBUG)
            Debug.debug(getClass(), "last include URI is null");
          
          _resource = _env.getResourceHandlerFor(_uri).getResourceObject(
              _uri);
          _uri = _resource.getURI();
          if(Debug.DEBUG)
            Debug.debug(getClass(), "resource: " + _uri);
        } else {
          Include last = _owner.getLastInclude();
          String uri = last.getUri();
          if(Utils.isRelativePath(_uri)) {
            _uri = Utils.getRelativePath(uri, _uri, true);
            if(Debug.DEBUG)
              Debug.debug(getClass(), "relative resource (last include - " + last.getUri() + " ): " + _uri);
            _resource = _env.getResourceHandlerFor(_uri)
                .getResourceObject(_uri);
            _uri = _resource.getURI();
          } else {
            if(Debug.DEBUG)
              Debug.debug(getClass(), "absolute resource (last include - " + last.getUri() + " ): " + _uri);
            _resource = _env.getResourceHandlerFor(_uri)
                .getResourceObject(_uri);
            _uri = _resource.getURI();
          }
        }
      }
      if(process){
        Dom4jProcessor proc = new Dom4jProcessor(_env.getObjectFactory());
        input = _resource.getInputStream();
        return proc.process(Utils.replaceVars(_ctx == null ? SotoIncludeContext.currentState().getTemplateContext() : _ctx, input, _resource.getURI()));
      }
      else{
        return _resource;
      }
    } catch(ProcessingException e) {
      Debug.debug(getClass(), "--------> Error including : " + _uri + " - " + e.getMessage());      
      throw new ConfigurationException("Could not process resource for: "
          + _uri + "(" + e.getMessage() + ")", e);
    } catch(IOException e) {
      throw new ConfigurationException("Could not get resource for: " + _uri, e);
    }finally {
      if(input != null) {
        try{
          input.close();
        }catch(IOException e){}
      }
    }      
  }*/
}
