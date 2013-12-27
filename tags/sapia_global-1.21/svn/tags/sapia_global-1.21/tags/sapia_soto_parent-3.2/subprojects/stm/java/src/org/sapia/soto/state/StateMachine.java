package org.sapia.soto.state;

import java.util.Iterator;
import org.sapia.soto.Debug;
import org.sapia.soto.state.config.Globals;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;

/**
 *
 * An instance of this class holds <code>State</code> instances. Each
 *
 * <code>State</code> is bound under its unique identifier.
 *
 *
 *
 * @author Yanick Duchesne
 *
 * <dl>
 *
 * <dt><b>Copyright: </b>
 *
 * <dd>Copyright &#169; 2002-2003 <a
 *
 * href="http://www.sapia-oss.org">Sapia Open Source Software </a>. All
 *
 * Rights Reserved.</dd>
 *
 * </dt>
 *
 * <dt><b>License: </b>
 *
 * <dd>Read the license.txt file of the jar or visit the <a
 *
 * href="http://www.sapia-oss.org/license.html">license page </a> at the
 *
 * Sapia OSS web site</dd>
 *
 * </dt>
 *
 * </dl>
 *
 */

public class StateMachine implements ObjectHandlerIF {
  
  private static final int TYPE_PRE = 0;
  
  private static final int TYPE_POST = 1;
  
  private static final int TYPE_ERR = 2;
  
  StaticContext _stmContext = new StaticContext();
  
  private boolean _init;
  
  public StateMachine() {
    
  }
  
  /**
   *
   * Returns the <code>State</code> that corresponds to the given identifier,
   *
   * or <code>null</code> if no <code>State</code> is found.
   *
   *
   *
   * @param id
   *
   * the identifier of the desired state.
   *
   * @return a <code>State</code>.
   *
   */
  
  public State getState(String id) {
    
    StateHolder sh = (StateHolder) _stmContext.getStates().get(id);
    
    if (sh == null) {
      
      return null;
      
    }
    
    return sh.getState();
    
  }
  
  /**
   *
   * Adds the given <code>State</code> to this instance.
   *
   *
   *
   * @param state
   *
   * a <code>State</code> instance.
   *
   * @throws ConfigurationException
   *
   */
  
  public void addState(State state) throws ConfigurationException {
    
    addStateHolder(new StateHolder(state));
    
  }
  
  /**
   *
   * Adds the given <code>Module</code> to this instance.
   *
   *
   *
   * @param mod
   *
   * a <code>Module</code> instance.
   *
   * @throws ConfigurationException
   *
   */
  
  public void addModule(Module mod) throws ConfigurationException {
    
    if (_init) {
      
      throw new IllegalStateException(
        
        "Modules must be added prior to initialization");
      
    }
    
    if (mod.getName() != null) {
      
      if (_stmContext.getModules().get(mod.getName()) != null) {
        
        throw new ConfigurationException("Module already exists for: "
          
          + mod.getName());
        
      }
      
      if (mod.getStateMachine(false) != null) {
        
        mod.getStateMachine(false)._stmContext.setParent(this);
        
      }
      
      _stmContext.getModules().put(mod.getName(), mod);
      
    } else {
      
      if (mod.getStateMachine(false) != null) {
        
        mod.getStateMachine(false)._stmContext.setParent(this);
        
      }
      
      _stmContext.getAnonymousModules().add(mod);
      
    }
    
  }
  
  /**
   *
   * @param gb
   *
   * a <code>Globals</code> instance.
   *
   */
  
  public void addGlobals(Globals gb) {
    
    _stmContext._globals.merge(gb);
    
  }
  
  /**
   *
   * Adds a state listener to this instance.
   *
   *
   *
   * @param listener
   *
   * a <code>StateExecListener</code>.
   *
   */
  
  public void addStateExecListener(StateExecListener listener) {
    
    _stmContext._listeners.add(listener);
    
  }
  
  /**
   *
   * Merges the given <code>StateMachine</code> to this instance; in short,
   *
   * the given engine's <code>State</code> s will be added to this instance.
   *
   *
   *
   * @param machine
   *
   * the <code>StateMachine</code> to merge with this instance.
   *
   *
   *
   * @throws ConfigurationException
   *
   * if the given machine holds a state whose identifier is already
   *
   * taken by a flow within this instance.
   *
   */
  
  public void merge(StateMachine machine) throws ConfigurationException {
    
    Iterator itr = machine._stmContext._states.values().iterator();
    
    while (itr.hasNext()) {
      
      addStateHolder((StateHolder) itr.next());
      
    }
    
    itr = machine._stmContext._modules.values().iterator();
    
    while (itr.hasNext()) {
      
      addModule((Module) itr.next());
      
    }
    
    itr = machine._stmContext._anonymousModules.iterator();
    
    while (itr.hasNext()) {
      
      addModule((Module) itr.next());
      
    }
    
    _stmContext._globals.merge(machine._stmContext._globals);
    
  }
  
  /**
   *
   * Initializes this instance.
   *
   */
  
  public void init() {
    
    String id;
    
    Iterator itr = _stmContext._states.keySet().iterator();
    
    StateHolder sh;
    
    State st;
    
    while (itr.hasNext()) {
      
      id = (String) itr.next();
      
      sh = (StateHolder) _stmContext._states.get(id);
      
      st = sh.getState();
      
      st = _stmContext._globals.applySteps(st, _stmContext._inheritGlobals);
      
      sh.setState(st);
      
      sh.setVisible(_stmContext._globals.applyRestriction(st,
        _stmContext._inheritGlobals));
      
    }
    
    _stmContext.init();
    
    _init = true;
    
  }
  
  /**
   *
   * Execute a state and returns the latter's execution result in a
   *
   * <code>State</code> instance.
   *
   *
   *
   * @param stateId
   *
   * the identifier of the flow to execute.
   *
   * @param ctx
   *
   * an execution <code>Context</code>.
   *
   * @return the <code>Result</code> that results from the state's execution.
   *
   * @throws UnknownStateException
   *
   * if no state could be found for the given identifier.
   *
   * @throws StateExecException
   *
   * if an error was generated but not handled.
   *
   *
   *
   * @see Result#handleError()
   *
   */
  
  public Result execute(String stateId, Context ctx)
  
  throws UnknownStateException, StateExecException {
    
    return doExecute(new StatePath(stateId), null, ctx, true);
    
  }
  
  /**
   *
   * Execute a state and returns the latter's execution result in a
   *
   * <code>State</code> instance.
   *
   *
   *
   * @param stateId
   *
   * the identifier of the flow to execute.
   *
   * @param module
   *
   * the name of the module to which the execution should be delegated.
   *
   * @param ctx
   *
   * an execution <code>Context</code>.
   *
   * @return the <code>Result</code> that results from the state's execution.
   *
   * @throws UnknownStateException
   *
   * if no state could be found for the given identifier.
   *
   * @throws StateExecException
   *
   * if an error was generated but not handled.
   *
   *
   *
   * @see Result#handleError()
   *
   */
  
  public Result execute(String stateId, String module, Context ctx)
  
  throws UnknownStateException, StateExecException {
    
    return doExecute(new StatePath(stateId, module), null, ctx, true);
    
  }
  
  /**
   *
   * Execute a state and returns the latter's execution result in a
   *
   * <code>State</code> instance.
   *
   *
   *
   * @param path
   *
   * the <code>StatePath</code> identifying the state to execute.
   *
   * @param ctx
   *
   * an execution <code>Context</code>.
   *
   * @return the <code>Result</code> that results from the state's execution.
   *
   * @throws UnknownStateException
   *
   * if no state could be found for the given identifier.
   *
   * @throws StateExecException
   *
   * if an error was generated but not handled.
   *
   *
   *
   * @see Result#handleError()
   *
   */
  
  public Result execute(StatePath path, Context ctx) throws UnknownStateException, StateExecException {
    
    return doExecute(path, null, ctx, true);
    
  }
  
  StaticContext getStmContext() {
    
    return _stmContext;
    
  }
  
  protected void addStateHolder(StateHolder sh) throws ConfigurationException {
    
    if (sh.getState().getId() != null) {
      
      if (_stmContext._states.get(sh.getState().getId()) != null) {
        
        throw new ConfigurationException("State already exists for: "
          
          + sh.getState().getId());
        
      }
      
      _stmContext._states.put(sh.getState().getId(), sh);
      
    } else {
      
      throw new ConfigurationException("'id' attribute not specified on state");
      
    }
    
  }
  
  Result doExecute(StatePath path, Result ancestor, Context ctx, boolean fromOutside) throws UnknownStateException, StateExecException {
    
    if (!_init) {
      throw new IllegalStateException("State machine not initialized; call init() prior to using it.");
    }
    if (path.getStateId() == null) {
      throw new IllegalArgumentException("State id cannot be null");
    }
    
    if (path.isAbsolute()) {
      if (_stmContext.getParent() != null) {
        return _stmContext.getParent().doExecute(path, ancestor, ctx,
          fromOutside);
      } else {
        path.setAbsolute(false);
      }
    }
    
    if (path.hasNextPathToken()) {
      if (Debug.DEBUG) {
        Debug.debug(getClass(), "Processing module path...");
      }
      
      if(path.isNextParent()){
        if(_stmContext._parent == null){
          throw new UnknownStateException("No parent module found: " + path.toString());
        } else{
          // consume next token
          path.nextPathToken();
          StateMachine currentParent = _stmContext._parent;
          while(currentParent._stmContext._name == null){
            if(currentParent._stmContext._parent == null){
              throw new UnknownStateException("No parent module found: " + path.toString());
            } else{
              currentParent = currentParent._stmContext._parent;
            }
          }
          return currentParent.execute(path, ancestor);
        }
      }
      
      Module mod = getModule(path,true);
      if (mod == null) {
        if (Debug.DEBUG) {
          Debug.debug(getClass(), "Module not found: " + path);
          Debug.debug(_stmContext.toString());
        }
        
        UnknownStateException uke = new UnknownStateException("Module does not exist: " + path);
        
        Result toReturn = new Result(ancestor, this, ctx);
        toReturn.error(uke);
        Result handled = _stmContext._globals.handleError(toReturn);
        
        if (handled.isError() && !handled.isErrorHandled()) {
          throw new StateExecException(handled.getError());
        }
        
        return toReturn;
      }
      
      StateMachine stm = mod.getStateMachine(true);
      
      // executing enter steps...
      // -->>>>>> execute enter step of this state machine before
      // delegating to sub state machine<<<--
      
      Result rs = new Result(ancestor, this, ctx);
      
      if (stm._stmContext._inheritGlobals && fromOutside) {

        _stmContext._globals.execEnter(rs, false);

      }
      Result res=rs;
      if(!rs.isAborted() && !rs.isError()){
        res = stm.doExecute(path, rs, ctx, true);
      }
      // <<<<<<<<<<<<--
      
      // executing "exit" steps...
      // -->>>>>> execute exit step of this state machine before
      // delegating to sub state machine<<<--
      
      if (stm._stmContext._inheritGlobals && fromOutside) {
        if (res.isAborted()) {
          if (Debug.DEBUG) {
            Debug.debug("Preparing for exit triggers; current result aborted. Spawning child.");
          }
          
          Result child = new Result(res, this, res.getContext());
          
          try {
            _stmContext._globals.execExit(child, false);

            if (child.isError()) {
              res.error(child.getError());
            }
          } catch (RuntimeException e) {
            res.error(e);
          }
        }else{
          _stmContext._globals.execExit(res, false);
        }
      }
      
      // <<<<<<<<<<<<--
      return res;
    }
    
    if (Debug.DEBUG) {
      Debug.debug(getClass(), "Executing state in module: " +(_stmContext._name == null ? "anonymous" : _stmContext._name));
    }
    
    Result rs = new Result(ancestor, this, ctx);
    // executing enter steps...
    if (fromOutside){

      _stmContext._globals.execEnter(rs, false);

    }
    
    if(!rs.isError() && !rs.isAborted()){
      rs.setNextStateId(path.getStateId());
    }
   
    StateHolder sh;
    State st;
    int count = 0;
    while ((rs.getNextStateId() != null) && !rs.isError() && !rs.isAborted()) {
      sh = (StateHolder) _stmContext.getStateFor(rs.getNextStateId(), false);
      if (sh == null) {
        StateMachine stm = _stmContext.getStateMachineFor(rs.getNextStateId(),true);
        
        if (stm != null) {
          /*
           * rs.setCurrentStateId(rs.getNextStateId());
           *
           * rs.reset();
           */
          rs = stm.execute(rs.getNextStateId(), null, rs.getContext());
          rs.setStateMachine(this);
          continue;
        }
      }
      if (sh == null) {
        if (Debug.DEBUG) {
          Debug.debug(getClass(), "State not found: " + rs.getNextStateId());
          Debug.debug(_stmContext.toString());
        }
        String modulePath = _stmContext.getModulePath().toString();
        UnknownStateException uste = new UnknownStateException(rs.getNextStateId() + " under module: " +
          
          (modulePath.length() == 0 ? "Root" : modulePath));
        rs = handleError(rs, uste);
        break;
      }
      
      st = sh.getState();
      if ((count == 0) && fromOutside && !sh.isVisible()) {
        throw new StateAccessException("State '" + st.getId()
        + "' cannot be executed by client application");
      }
      
      rs.setCurrentStateId(rs.getNextStateId());
      rs.reset();
      notifyListeners(st, rs, TYPE_PRE);
      if (Debug.DEBUG) {
        Debug.debug(getClass(), "Executing: " + st.getId() + " ("
          + st.getClass().getName()
          + ")");
      }
      try {
        st.execute(rs);
      } catch (RuntimeException e) {
        rs.error(e);
      }
      if (rs.isError()) {
        notifyListeners(st, rs, TYPE_ERR);
      } else {
        notifyListeners(st, rs, TYPE_POST);
      }
      count++;
    }
    
    // executing "exit" steps...
    if (fromOutside) {
      if (rs.isAborted()) {
        if (Debug.DEBUG) {
          Debug
            .debug("Preparing for exit triggers; current result aborted. Spawning child.");
        }
        Result child = new Result(rs, this, rs.getContext());
        try {
          _stmContext._globals.execExit(child, false);
          if (child.isError()) {
            rs.error(child.getError());
          }
        } catch (RuntimeException e) {
          rs.error(e);
        }
      } else {
        _stmContext._globals.execExit(rs, false);
      }
    }
    if (rs.isError() && !rs.isErrorHandled()) {
      if (Debug.DEBUG) {
        Debug.debug(getClass(), "handling error");
      }
      Result handled;
      
      if (rs.isHandlingError()) {
        throw new StateExecException(rs.getError());
      }
      
      rs.handlingError();
      handled = _stmContext._globals.handleError(rs);
      if (handled.isError() && !handled.isErrorHandled()) {
        if (Debug.DEBUG) {
          Debug.debug(getClass(), "handling error");
        }
        throw new StateExecException(handled.getError());
      }
      return handled;
    }
    return rs;
  }
  
  Result execute(StatePath path, Result rs) throws StateExecException {
    
    return doExecute(path, rs, rs.getContext(), false);
    
  }
  
  Result execute(String stateId, String module, Result rs)
  
  throws StateExecException {
    
    return execute(new StatePath(stateId, module), rs);
    
  }
  
  private Module getModule(StatePath path,boolean lookInParent) {
    
    String name = null;
    
    Module mod = null;
    
    if (path.hasNextPathToken()) {
      
      boolean first = path.isFirstToken();
      name = path.nextPathToken();
      mod = (Module) _stmContext._modules.get(name);
      

      
      if (mod == null) {
        for(int i = 0; i < _stmContext._anonymousModules.size(); i++){
          Module anon = (Module)_stmContext._anonymousModules.get(i);
          path.back();
          mod = anon.getStateMachine(true).getModule(path.copy(),false);
          path.forward();
        }
      }
      
      if (mod == null) {
        if (_stmContext._inheritModules && (_stmContext._parent != null) && first && lookInParent) {
          path.back();
          mod = _stmContext._parent.getModule(path.copy(),true);
          path.forward();
        }
      }
    }
    
    if (Debug.DEBUG && mod!=null) {
      Debug.debug(getClass(), "Got module: " + name);
    }
    
    return mod;
    
  }
  
  private void notifyListeners(State f, Result st, int type) {
    
    StateExecListener listener;
    
    for (int i = 0; i < _stmContext._listeners.size(); i++) {
      
      listener = (StateExecListener) _stmContext._listeners.get(i);
      
      if (type == TYPE_PRE) {
        
        listener.onPreExec(st, f.getId());
        
      } else if (type == TYPE_POST) {
        
        listener.onPostExec(st, f.getId());
        
      } else {
        
        listener.onError(st, f.getId(), st.getError());
        
      }
      
    }
    
  }
  
  private Result handleError(Result rs, Exception e) throws StateExecException {
    
    if (rs.getAncestor() != null) {
      
      if (rs.getAncestor().isError()) {
        
        throw new StateExecException(rs.getAncestor().getError());
        
      } else {
        
        if (!rs.isError()) {
          
          rs.error(e);
          
        }
        
        throw new StateExecException(rs.getError());
        
      }
      
    }
    
    Result errorRes = rs.newInstance();
    
    errorRes.setStateMachine(this);
    
    if (e != null) {
      
      errorRes.error(e);
      
    }
    
    Result handled = _stmContext._globals.handleError(errorRes);
    
    if (handled.isError() && !handled.isErrorHandled()) {
      
      throw new StateExecException(handled.getError());
      
    } else {
      
      return handled;
      
    }
    
  }
  
  /**
   *
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(java.lang.String,
   *
   * java.lang.Object)
   *
   */
  
  public void handleObject(String name, Object obj)
  
  throws ConfigurationException {
    
    if (obj instanceof State) {
      
      State state = (State) obj;
      
      addState(state);
      
    } else if (obj instanceof Globals) {
      
      _stmContext._globals.merge((Globals) obj);
      
    } else if (obj instanceof Module) {
      
      addModule((Module) obj);
      
    }
    
  }
  
}
