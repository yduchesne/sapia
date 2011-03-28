package org.sapia.soto;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sapia.soto.config.Application;
import org.sapia.soto.config.AssignTag;
import org.sapia.soto.config.Choose;
import org.sapia.soto.config.If;
import org.sapia.soto.config.RegisterLifeCycleTag;
import org.sapia.soto.config.SelectTag;
import org.sapia.soto.config.Unless;
import org.sapia.soto.config.IncludeTag;
import org.sapia.soto.config.ParamRef;
import org.sapia.soto.config.ServiceRef;
import org.sapia.soto.config.ServiceTag;
import org.sapia.soto.config.NewTag;
import org.sapia.soto.config.SotoIncludeContext;
import org.sapia.soto.util.CompositeObjectFactoryEx;
import org.sapia.soto.util.Def;
import org.sapia.soto.util.Defs;
import org.sapia.soto.util.Namespace;
import org.sapia.resource.ResourceHandler;
import org.sapia.resource.ResourceNotFoundException;
import org.sapia.soto.util.Utils;
import org.sapia.util.text.SystemContext;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.xml.ProcessingException;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.CreationStatus;
import org.sapia.util.xml.confix.Dom4jProcessor;
import org.sapia.util.xml.confix.ObjectCreationException;
import org.sapia.util.xml.confix.ReflectionFactory;

/**
 * This factory creates the objects that belong to the "soto" namespace.
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
public class SotoApplicationFactory extends CompositeObjectFactoryEx {
  public static final String APP         = "app";
  public static final String DEFS        = "defs";
  public static final String NAMESPACE   = "namespace";
  public static final String SERVICE     = "service";
  public static final String SERVICE_REF = "serviceRef";
  public static final String INCLUDE     = "include";
  public static final String IF          = "if";
  public static final String UNLESS      = "unless";
  public static final String CHOOSE      = "choose";
  public static final String DEBUG       = "debug";
  public static final String ENV         = "env";
  public static final String NEW         = "new";
  public static final String INJECT      = "inject";
  public static final String PARAM_REF   = "param";
  public static final String SELECT      = "select";
  public static final String REGISTER_LIFE_CYCLE = "registerLifeCycle";
  
  static final Set    UNSUPPORTED_DEF_SCHEMES = new HashSet();
  static final String DEFAULT_DEF_FILE = "defs.xml";
  static final String XML_EXT = ".xml";
  static final String XML_PREFIX = "xml";
  static final String SOTO_SCHEME_OR_PREFIX = "soto";
  static final String HTTP_SCHEME           = "http";
  static final String SOTO_DEFS_PATH = "resource:/org/sapia/soto/defs/";
  
  static{
    UNSUPPORTED_DEF_SCHEMES.add(HTTP_SCHEME);
  }
  
  private SotoContainer      _container;
  private TemplateContextIF  _ctx;
  private ReflectionFactory  _fac        = new ReflectionFactory(new String[0]);
  private Map                _cachedUris = Collections.synchronizedMap(new HashMap());
  private FallbackFactory    _fallback;
  /**
   * Constructor for SotoApplicationFactory.
   */
  public SotoApplicationFactory(SotoContainer container) {
    _container = container;
    _fallback = new FallbackFactory();
    _fallback.loadSotoDefs();
    super.setMapToPrefix(true);
  }

  /**
   * Sets this instance's template context - holding name/value binding.
   * 
   * @param ctx
   *          a <code>TemplateContextIF</code>.
   */
  void setContext(TemplateContextIF ctx) {
    _ctx = ctx;
  }

  /**
   * Adds the given namespace to this instance.
   * 
   * @param defs
   *          a <code>Namespace</code>.
   */
  public void addNamespace(Namespace defs) throws ConfigurationException {
    registerDefs(defs);
  }

  public TemplateContextIF getTemplateContext() {
    if(_ctx == null) {
      return new SystemContext();
    }

    return _ctx;
  }

  /**
   * @see org.sapia.util.xml.confix.CompositeObjectFactory#newObjectFor(java.lang.String,
   *      java.lang.String, java.lang.String, java.lang.Object)
   */
  public CreationStatus newObjectFor(String prefix, String uri, String name,
      Object arg3) throws ObjectCreationException {
    try{
      return setUp(prefix, uri, name, doNewObjectFor(prefix, uri, name, arg3));
    }catch(ObjectCreationException e){
      try{
        return setUp(prefix, uri, name, _fallback.newObjectFor(prefix, uri, name, arg3));
      }catch(ObjectCreationException e2){
        throw e;
      }
    }
  }

  /**
   * @see org.sapia.soto.util.CompositeObjectFactoryEx#registerDefs(org.sapia.soto.util.Namespace)
   */
  public void registerDefs(Namespace defs) throws ConfigurationException {
    super.registerDefs(defs);
    if(Debug.DEBUG){
      Debug.debug("===> Definitions for: " + defs.getPrefix());
      List defList = defs.getDefs();
      Def def;
  
      for(int i = 0; i < defList.size(); i++) {
        def = (Def) defList.get(i);
        Debug.debug("   name: " + def.getName() + ", " + def.getClazz());
      }
    }
  }

  protected CreationStatus doNewObjectFor(String prefix,
                                          String uri,
                                          String name,
                                          Object parent) throws ObjectCreationException {
    CreationStatus toReturn;

    if(prefix != null && prefix.equals(XML_PREFIX)){
      // this is a hack since XML parser assign a http://... URI automatically for the elements
      // associated to the 'xml' prefix...
      uri = new StringBuffer(SOTO_SCHEME_OR_PREFIX).append(':').append(XML_PREFIX).toString();
    }

    loadFromUri(uri);

    if((prefix != null) && (prefix.length() > 0)) {
      if(prefix.equals(SOTO_SCHEME_OR_PREFIX)) {
        if(name.equals(SERVICE)) {
          toReturn = CreationStatus.create(new ServiceTag(_container));
        } else if(name.equals(SERVICE_REF)) {
          toReturn = CreationStatus.create(new ServiceRef(_container));
        } else if(name.equals(NAMESPACE)) {
          toReturn = CreationStatus.create(new Namespace());
        } else if(name.equals(APP)) {
          toReturn = CreationStatus.create(new Application(this));
        } else if(name.equals(DEFS)){
          toReturn = CreationStatus.create(new Defs(this));
        } else if(name.equals(INCLUDE)) {
          toReturn = CreationStatus.create(new IncludeTag(env(), this));
        } else if(name.equals(IF)) {
          toReturn = CreationStatus.create(new If(this));
        } else if(name.equals(UNLESS)) {
          toReturn = CreationStatus.create(new Unless(this));
        } else if(name.equals(CHOOSE)) {
          toReturn = CreationStatus.create(new Choose(this));
        } else if(name.equals(NEW)) {
          toReturn = CreationStatus.create(new NewTag());          
        } else if(name.equals(INJECT)) {
          toReturn = CreationStatus.create(new AssignTag());          
        } else if(name.equals(DEBUG)) {
          toReturn = CreationStatus.create(new org.sapia.soto.config.Debug());
        } else if(name.equals(ENV)) {
          toReturn = CreationStatus.create(_container.toEnv());
        } else if(name.equals(PARAM_REF)){
          toReturn = CreationStatus.create(new ParamRef());
        } else if(name.equals(SELECT)){
          toReturn = CreationStatus.create(new SelectTag(_container.toEnv()));
        }else if(name.equals(REGISTER_LIFE_CYCLE)){
            toReturn = CreationStatus.create(new RegisterLifeCycleTag(_container));          
        } else {
          try{
            toReturn = super.newObjectFor(prefix, uri, name, parent);
          }catch(ObjectCreationException e){
            toReturn = _fallback.newObjectFor(prefix, uri, name, parent);
          }
        }
      } else {
        try{
          toReturn = super.newObjectFor(prefix, uri, name, parent);
        }catch(ObjectCreationException e){
          toReturn = _fallback.newObjectFor(prefix, uri, name, parent);
        }
      }
    } else {
      toReturn = _fac.newObjectFor(prefix, uri, name, parent);
    }

    return toReturn;
  }

  private void loadFromUri(String uri) throws ObjectCreationException{
    String scheme = chopScheme(uri);
    if(uri == null || uri.length() == 0 || (scheme != null && UNSUPPORTED_DEF_SCHEMES.contains(scheme))){
      return;
    }
    synchronized(_cachedUris){
      try{
        Boolean exists = (Boolean)_cachedUris.get(uri);
        if(exists == null){
          ResourceHandler handler = _container.getResourceHandlers().select(uri);
          if(handler == null){
            _cachedUris.put(uri, new Boolean(false));
            return;
          }
          if(scheme != null && scheme.startsWith(SOTO_SCHEME_OR_PREFIX)){
            String extUri = SOTO_DEFS_PATH + Utils.chopScheme(uri) + ".xml";
            try{
              // trying to load resource corresponding to soto:xxxx uri
              Dom4jProcessor proc = new Dom4jProcessor(this);
              proc.process(handler.getResource(extUri));
            }catch(ResourceNotFoundException e){
              // noop
            }catch(RuntimeException e){
              // noop
            }
          }
          else if(uri.endsWith(XML_EXT)){
            Dom4jProcessor proc = new Dom4jProcessor(this);
            proc.process(handler.getResource(uri));
          }
          else{
            try{
              String extUri = uri + '/' + DEFAULT_DEF_FILE;
              Dom4jProcessor proc = new Dom4jProcessor(this);
              proc.process(handler.getResource(extUri));
            }catch(ResourceNotFoundException e){
              Dom4jProcessor proc = new Dom4jProcessor(this);
              proc.process(handler.getResource(uri));
            }catch(RuntimeException e){
              Dom4jProcessor proc = new Dom4jProcessor(this);
              proc.process(handler.getResource(uri));
            }
          }
          _cachedUris.put(uri, new Boolean(true));
        }
      }catch(ResourceNotFoundException e){
        e.printStackTrace();
        if(Debug.DEBUG){
          Debug.debug(getClass(), "Could not process object definitions from URI: " + uri + "; ignoring - " + e.getMessage());
        }
        _cachedUris.put(uri, new Boolean(false));
      }catch(RuntimeException e){
        if(Debug.DEBUG){
          Debug.debug(getClass(), "Could not process object definitions from URI: " + uri + "; ignoring - " + e.getMessage());
        }
        _cachedUris.put(uri, new Boolean(false));
      }catch(MalformedURLException e){
        if(Debug.DEBUG){
          Debug.debug(getClass(), "Could not process object definitions from URI: " + uri + "; ignoring - " + e.getMessage());
        }        
        _cachedUris.put(uri, new Boolean(false));        
      }catch(IOException e){
        throw new ObjectCreationException("Could not load object definitions from URI: " + uri, e);
      }catch(ProcessingException e){
        if(Debug.DEBUG){
          Debug.debug(getClass(), "Could not process object definitions from URI: " + uri + "; ignoring - " + e.getMessage());
        }
        _cachedUris.put(uri, new Boolean(false));
      }
    }
  }

  private CreationStatus setUp(String prefix, String uri, String name, CreationStatus instance) {
    if(instance.getCreated() instanceof EnvAware) {
      ((EnvAware) instance.getCreated()).setEnv(env());
    }
    if(instance.getCreated() instanceof XmlAware){
      ((XmlAware) instance.getCreated()).setNameInfo(name, prefix, uri);
    }

    return instance;
  }

  private Env env(){
    Env env = SotoIncludeContext.currentEnv();
    if(env == null){
      env = _container.toEnv();
    }
    return env;
  }

  private String chopScheme(String uri){
    int idx = uri.indexOf(":");
    if(idx >= 0){
      return uri.substring(0, idx);
    }
    return null;
  }
}
