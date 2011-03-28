package org.sapia.soto.state;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sapia.soto.Debug;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.SotoConsts;
import org.sapia.soto.config.SotoIncludeContext;
import org.sapia.soto.config.SotoIncludeContextFactory;
import org.sapia.soto.util.Param;
import org.sapia.resource.Resource;
import org.sapia.resource.include.IncludeConfig;
import org.sapia.resource.include.IncludeContext;
import org.sapia.resource.include.IncludeState;
import org.sapia.util.text.MapContext;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;
import org.sapia.util.xml.confix.ObjectHandlerIF;

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
public class Module implements ObjectHandlerIF, EnvAware,
    ObjectCreationCallback, SotoConsts {
  private static final long     NOT_MODIFIED  = -1;
  private String                _name;
  private String                _uri;
  private StateMachine          _stm;
  private boolean               _inheritGlobals;
  private boolean               _inheritModules;
  private long                  _lastModified = NOT_MODIFIED;
  private Resource              _toMonitor;
  private Env                   _env;
  private List                  _params = new ArrayList();
  private TemplateContextIF     _templateContext;

  public Module() {
  }
  
  public Param createParam(){
    Param p = new Param();
    _params.add(p);
    return p;
  }

  public StateMachine getStateMachine(boolean reload) {
    if(reload && (_lastModified > NOT_MODIFIED)
        && (_lastModified != _toMonitor.lastModified())) {
      synchronized(this) {
        if(_lastModified != _toMonitor.lastModified()) {
          if(Debug.DEBUG) {
            Debug.debug("Module " + _uri + " modified (" + _toMonitor
                + "); reloading...");
          }
          InputStream input = null;
          try {
            IncludeConfig config = IncludeState.createConfig(SOTO_INCLUDE_KEY, new SotoIncludeContextFactory(_env, getTemplateContext()), _env.getResourceHandlers());
            StateMachine newStm = (StateMachine)IncludeState.createContext(_toMonitor.getURI(), config).include(getTemplateContext()); 
            newStm.getStmContext()._parent = _stm.getStmContext().getParent();
            newStm.getStmContext()._name = _stm.getStmContext()._name;            

            if(_inheritGlobals && (newStm.getStmContext()._globals != null)) {
              newStm.getStmContext()._globals.setParent(_stm.getStmContext()
                  .getParent().getStmContext()._globals);
            }

            if(_inheritModules) {
              newStm.getStmContext()._inheritModules = true;
            }
            if(_inheritGlobals){
              newStm.getStmContext()._inheritGlobals = true;
            }
            
            newStm.init();
            _stm = newStm;
          } catch(ConfigurationException e){
            throw new ModuleReloadRuntimeException(
                "Could not open state machine config file", e);            
          } catch(Exception e){
            throw new ModuleReloadRuntimeException(
                "Could not open state machine config file", e);
          }  
          
          /*} catch(IOException e){
            throw new ModuleReloadRuntimeException(
                "Could not open state machine config file", e);            
          } catch(ProcessingException e){
            throw new ModuleReloadRuntimeException(
                "Could not process state machine config file", e);            
          } */finally{
            if(input != null){
              try{
                input.close();
              }catch(IOException e){}
            }
          }

          if(Debug.DEBUG) {
            Debug.debug("Module reloaded");
          }

          _lastModified = _toMonitor.lastModified();
        }
      }
    }

    return _stm;
  }

  public String getName() {
    return _name;
  }

  public void setName(String name) {
    _name = name;
    if(_stm != null){
      _stm._stmContext.setName(name);
    }
  }

  public void setInheritGlobals(boolean inherit) {
    _inheritGlobals = inherit;
  }

  public boolean isInheritGlobals() {
    return _inheritGlobals;
  }

  public void setInheritModules(boolean inherit) {
    _inheritModules = inherit;
  }

  public boolean isInheritModules() {
    return _inheritModules;
  }

  public void setStateMachine(StateMachine stm) throws ConfigurationException {
    if(_stm != null) {
      throw new ConfigurationException("State machine already set for module: "
          + getName());
    }
    stm.getStmContext().setName(_name);
    stm.getStmContext()._inheritModules = _inheritModules;
    stm.getStmContext()._inheritGlobals = _inheritGlobals;
    _stm = stm;
  }

  /**
   * @see org.sapia.soto.EnvAware#setEnv(org.sapia.soto.Env)
   */
  public void setEnv(Env env) {
    _env = env;
  }

  public void setUri(String uri) {
    _uri = uri;
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    
    Debug.debug("======> loading module: " + _uri + " stm=" + _stm + ", env=" + _env);       
    if((_stm == null) && (_uri != null) && (_env != null)) {
      Debug.debug("======> loading module: " + _uri);
      Resource res = null;      
      try{
        IncludeConfig config = IncludeState.createConfig(SOTO_INCLUDE_KEY, new SotoIncludeContextFactory(_env, getTemplateContext()), _env.getResourceHandlers());        
        IncludeContext ctx = IncludeState.createContext(_uri, config);
        res = ctx.getResource();
        _stm = (StateMachine) ctx.include(getTemplateContext());
      }catch(Exception e){
        Debug.debug(getClass(), "--------> Error loading module: " + _uri);
        Debug.debug(getClass(), e);
        Debug.debug(getClass(), "<--------");
        if(e instanceof ConfigurationException){
          throw (ConfigurationException)e;
        }
        throw new ConfigurationException("Error loading module" + _uri, e);
      }
      _stm.getStmContext().setName(_name);
      Debug.debug("module: " + _name + " loaded");      
      
      _toMonitor = res;
      _lastModified = _toMonitor.lastModified();
    }
    else{
      if(_stm != null){
        _stm.getStmContext().setName(_name);
      }
    }

    return this;
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(java.lang.String,
   *      java.lang.Object)
   */
  public void handleObject(String name, Object obj)
      throws ConfigurationException {
    if(obj instanceof StateMachine) {
      setStateMachine((StateMachine) obj);
    }
  }
  
  private TemplateContextIF getTemplateContext(){
    if(_templateContext != null){
      return _templateContext;
    }
    Map params = new HashMap();
    for(int i = 0; i < _params.size(); i++){
      Param p = (Param)_params.get(i);
      if(p.getName() != null && p.getValue() != null){
        params.put(p.getName(), p.getValue());
      }
    }
    _templateContext = new MapContext(params, SotoIncludeContext.currentTemplateContext(), false);    
    return _templateContext;
  }
}
