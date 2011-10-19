package org.sapia.soto.state.config;

import java.util.ArrayList;
import java.util.List;

import org.sapia.soto.Debug;
import org.sapia.soto.state.Err;
import org.sapia.soto.state.ErrorHandler;
import org.sapia.soto.state.ExecContainer;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.State;

/**
 * Applies global behavior modifications/variations to states according to a
 * pattern matching notation.
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
public class Globals {
  private List          _preExecs      = new ArrayList();
  private List          _postExecs     = new ArrayList();
  private ExecContainer _enter         = new StepContainer();
  private ExecContainer _exit          = new StepContainer();
  private List          _privates      = new ArrayList();
  private ErrorHandlers _errorHandlers = new ErrorHandlers();
  private Globals       _parent;

  public Globals() {
  }

  public void setParent(Globals parent) {
    _parent = parent;
  }

  public StateSet createPreExec() {
    StateSet preExec = new StateSet();
    _preExecs.add(preExec);

    return preExec;
  }

  public StateSet createPrivate() {
    StateSet priv = new StateSet();
    _privates.add(priv);

    return priv;
  }

  public StateSet createPostExec() {
    StateSet postExec = new StateSet();
    _postExecs.add(postExec);

    return postExec;
  }

  public ExecContainer createEnter() {
    return _enter;
  }

  public ExecContainer createExit() {
    return _exit;
  }

  public ErrorHandlers createErrorHandlers() {
    return _errorHandlers;
  }

  /**
   * Executes the steps at entry of state machine steps.
   * 
   * @param res
   *          a <code>Result</code>.
   * @param execParent if <code>true</code>, indicates that the parent state
   * machine's triggers should in addition be invoked.
   */
  public void execEnter(Result res, boolean execParent) {
    if(res.isAborted() || res.isError()) {
      return;
    }
    _enter.execute(res);    
    if(res.isAborted() || res.isError()) {
      return;
    }
    if(_parent != null && execParent) {
      if(Debug.DEBUG) Debug.debug(getClass(), "Executing parent enter triggers");
      _parent.execEnter(res, execParent);
    }
  }

  /**
   * Executes the steps at exit of state machine steps.
   * 
   * @param res
   *          a <code>Result</code>.
   * @param execParent if <code>true</code>, indicates that the parent state
   * machine's triggers should in addition be invoked.
   */
  public void execExit(Result res, boolean execParent) {
    if(res.isAborted() || res.isError()) {
      return;
    }
    _exit.execute(res);    
    if(res.isAborted() || res.isError()) {
      return;
    }          
    if(_parent != null && execParent) {
      if(Debug.DEBUG) Debug.debug(getClass(), "Executing parent exit triggers");      
      _parent.execExit(res, execParent);
    }
  }

  /**
   * Merges the content of the given instance with this instance.
   * 
   * @param gb
   *          other <code>Globals</code>.
   */
  public void merge(Globals gb) {
    _enter.addExecutables(gb._enter.getExecutables());
    _exit.addExecutables(gb._exit.getExecutables());
    _preExecs.addAll(gb._preExecs);
    _postExecs.addAll(gb._postExecs);
    _privates.addAll(gb._privates);
    _errorHandlers.addErrorHandlers(gb._errorHandlers.getErrorHandlers());
  }

  /**
   * Applies the visibility restriction corresponding to the given pattern.
   * 
   * @param state
   *          a <code>State</code>
   * @return <code>true</code> if the given state is public.
   */
  public boolean applyRestriction(State state, boolean applyParent) {
    StateSet current;
    boolean pub = true;

    for(int i = 0; i < _privates.size(); i++) {
      current = (StateSet) _privates.get(i);

      if(current.matches(state)) {
        pub = false;
      }
    }

    if(pub && (_parent != null && applyParent)) {
      pub = _parent.applyRestriction(state, applyParent);

      return pub;
    } else {
      return pub;
    }
  }

  /**
   * Applies the pre-executed and post-executed steps to the given state.
   * 
   * @param state
   *          a <code>State</code>.
   * @return the resulting <code>State</code> (might be the original one or a
   *         proxy).
   */
  public State applySteps(State state, boolean execParent) {
    GlobalWrapper wrapper = null;
    StateSet current;

    for(int i = 0; i < _preExecs.size(); i++) {
      current = (StateSet) _preExecs.get(i);

      if(current.matches(state)) {
        wrapper = wrapper(wrapper, state);
        wrapper.addPre(current.getSteps());
      }
    }

    for(int i = 0; i < _postExecs.size(); i++) {
      current = (StateSet) _postExecs.get(i);

      if(current.matches(state)) {
        wrapper = wrapper(wrapper, state);
        wrapper.addPost(current.getSteps());
      }
    }

    if(_parent != null && execParent) {
      if(wrapper != null) {
        return _parent.applySteps(wrapper, execParent);
      } else {
        return _parent.applySteps(state, execParent);
      }
    }

    if(wrapper == null) {
      return state;
    } else {
      return wrapper;
    }
  }

  /**
   * @param errorResult
   *          the result that holds the error to handle.
   * @return the Result of the error handling.
   */
  public Result handleError(Result errorResult) {
    Result newResult = errorResult.newInstance();
    Err err = errorResult.handleError();
    newResult.getContext().push(err);
    try{
      if(doHandleError(newResult)) {
        if(Debug.DEBUG){
          Debug.debug(getClass(), "error was matched; error=" + newResult.isError());      
        }
        return newResult;
      } else {
        if(Debug.DEBUG){
          Debug.debug(getClass(), "error was not matched");            
        }
        Result newErrorResult = errorResult.newInstance();
        newErrorResult.error(err);
        return newErrorResult;
      }
    }catch(RuntimeException e){
      if(Debug.DEBUG){
        Debug.debug("Problem occurred while handling error...");
        Debug.debug(getClass(),  e);
      }
      newResult.error("Caught runtime exception while handling error", e);
      return newResult;
    }
  }

  protected boolean doHandleError(Result result) {
    ErrorHandler handler;
    List errorHandlers = _errorHandlers.getErrorHandlers();
    if(Debug.DEBUG){
      Debug.debug(getClass(), "Handling error " + result.getContext().currentObject());
      try{
        ((Err)result.getContext().currentObject()).getThrowable().printStackTrace();
      }catch(ClassCastException e){
        
      }      
      Debug.debug(getClass(), "No error handlers specified in state machine");

    }
    
    for(int i = 0; i < errorHandlers.size(); i++) {
      handler = (ErrorHandler) errorHandlers.get(i);
      if(Debug.DEBUG){
        Debug.debug(getClass(), "Handler: " + handler);      
      }
      if(handler.handle(result)) {
        if(Debug.DEBUG){
          Debug.debug(getClass(), "Handler could process error completed");              
        }
        return true;
      }
    }
    if(_parent != null) {
      if(_parent.doHandleError(result)) {
        return true;
      }
    }
    if(Debug.DEBUG){
      Debug.debug(getClass(), "No error handler matched...");
    }
    return false;
  }

  private GlobalWrapper wrapper(GlobalWrapper w, State state) {
    if(w == null) {
      return new GlobalWrapper(state);
    }

    return w;
  }
}
