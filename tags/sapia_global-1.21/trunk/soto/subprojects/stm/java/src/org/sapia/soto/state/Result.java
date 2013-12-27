package org.sapia.soto.state;

/**
 * An instance of this class results from the execution of a given state.
 * 
 * @see org.sapia.soto.state.StateMachine#execute(String, Context)
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
public class Result {
  
  public static final int STATUS_NONE             = 0;
  public static final int STATUS_HANDLING_ERROR   = 1;
  public static final int STATUS_HANDLED_ERROR    = 2;
  public static final int STATUS_ABORTED          = 4;  
  
  private String       _nextState;
  private String       _currentState;
  private int          _status;
  private Err          _err;
  private Context      _ctx;
  private StateMachine _stm;
  private Result       _ancestor;
  private ResultToken  _token = new ResultToken();

  public Result(StateMachine engine, Context ctx) {
    _ctx = ctx;
    _stm = engine;
  }

  public Result(Result ancestor, StateMachine engine, Context ctx) {
    this(engine, ctx);
    _ancestor = ancestor;
    if(_ancestor != null){
      _nextState = ancestor._nextState;
      _currentState = ancestor._currentState;
      _token = ancestor.getToken();
      if((ancestor._status & STATUS_HANDLING_ERROR) != 0){
        _status = _status | STATUS_HANDLING_ERROR;
      }
    }
  }
  
  /**
   * @return the <code>ResultToken</code> held by this instance.
   */
  public ResultToken getToken(){
    return _token;
  }
  
  /**
   * @return the <code>StaticContext</code> of the <code>StateMachine</code> 
   * to which this instance corresponds.
   */
  public StaticContext getStaticContext(){
    return _stm.getStmContext();
  }

  /**
   * @return the identifier of the next state to execute, or <code>null</code>
   *         if there is no other state to execute.
   */
  public String getNextStateId() {
    return _nextState;
  }

  /**
   * @param id
   *          sets the identifier of the next state to execute.
   * @see #getNextStateId()
   */
  public void setNextStateId(String id) {
    _nextState = id;
  }

  /**
   * Indicates that state execution should be stopped. This method should be
   * called when no error has been generated, but state execution must still be
   * stopped.
   */
  public void abort() {
    _status = _status | STATUS_ABORTED;
  }

  /**
   * @return <code>true</code> if the abort() method has been called on this
   *         instance.
   * 
   * @see #abort()
   */
  public boolean isAborted() {
    return (_status & STATUS_ABORTED) != 0;
  }

  /**
   * @return <code>true</code> if this instance holds an <code>Err</code>
   *         instance.
   */
  public boolean isError() {
    return _err != null;
  }

  /**
   * @return the <code>Context</code> in which this instance has been created.
   */
  public Context getContext() {
    return _ctx;
  }

 
  /**
   * @param id
   *          sets the ID of the current state. Applications should not deal
   *          with this method.
   */
  public void setCurrentStateId(String id) {
    _currentState = id;
  }

  /**
   * @return the identifier of the currently executing state.
   */
  public String getCurrentStateId() {
    return _currentState;
  }
  
 /**
   * Signals that an error has occured.
   * 
   * @param msg
   *          an error message.
   */
  public void error(String msg) {
    error(new Err(msg));
  }

  /**
   * Signals that an error has occured.
   * 
   * @param err
   *          an <code>Err</code> instance.
   */
  public void error(Err err) {
    if((_status & STATUS_HANDLED_ERROR) != 0) _status = _status ^ STATUS_HANDLED_ERROR;    
    _err = err;
  }  

  /**
   * Signals that an error has occurred.
   * 
   * @param msg
   *          an error message.
   * @param err
   *          a <code>Throwable</code> instance.
   */
  public void error(String msg, Throwable err) {
    error(new Err(msg, err));
  }

  /**
   * @param err
   *          a <code>Throwable</code>.
   */
  public void error(Throwable err) {
    error(new Err(err));
  }

  /**
   * @return the <code>Err</code> object that contains information about the
   *         error that occurred.
   * 
   * @throws IllegalStateException
   *           if no error occurred.
   * 
   * @see #isError()
   */
  public Err handleError() throws IllegalStateException {
    if((_status & STATUS_HANDLED_ERROR) == 0) _status = _status | STATUS_HANDLED_ERROR;    
    return _err;
  }

  /**
   * @return <code>true</code> if the error that this instance contains (if
   *         any) was handled.
   * 
   * @see #isError()
   * @see #handleError()
   */
  public boolean isErrorHandled() {
    return (_status & STATUS_HANDLED_ERROR) != 0;
  }
  
  /**
   * @return <code>true</code> if this instance's error is being handled
   * by an <code>ErrorHandler</code>.
   */
  public boolean isHandlingError(){
    return (_status & STATUS_HANDLING_ERROR) != 0;    
  }
  
  void handlingError(){
    if((_status & STATUS_HANDLING_ERROR) == 0) _status = _status | STATUS_HANDLING_ERROR;
  }
  
  
  /**
   * Executes the state whose ID is given.
   * 
   * @param stateId
   *          the identifier of a state.
   * @param module
   *          the name of the module that the state is part of, or
   *          <code>null</code> if no such module exists.
   * @throws UnknownStateException
   *           if no state exists for the given identifier.
   */
  public void exec(String stateId, String module) throws UnknownStateException,
      StateExecException {
    _stm.execute(stateId, module, this);
  }
  
  /**
   * @see exec(StatePath)
   */
  public void exec(String statePath) throws UnknownStateException,
      StateExecException{
    exec(StatePath.parse(statePath));
  }

  /**
   * @param path
   *          the <code>StatePath</code> corresponding to the state to
   *          execute.
   * @throws UnknownStateException
   *           if no state exists for the given identifier.
   */
  public void exec(StatePath path) throws UnknownStateException,
      StateExecException {
    _stm.execute(path, this);
  }

  /**
   * Sets the next state identifier to null.
   */
  void reset() {
    _nextState = null;
  }

  void setStateMachine(StateMachine stm) {
    _stm = stm;
  }

  /**
   * @return an <code>Err</code> instance, or <code>null</code> if this
   *         state holds to <code>Err</code> instance.
   */
  Err getError() {
    return _err;
  }

  /**
   * @return the ancestor <code>Result</code> of this instance.
   * @see #newInstance()
   */
  public Result getAncestor() {
    return _ancestor;
  }

  /**
   * Returns a new instance of this class, holding the <code>Context</code>
   * that this instance holds, and associated to the same
   * <code>StateMachine</code>.
   * <p>
   * The returned instance will have this instance as an ancestor, and the
   * same current state ID as this instance.
   * 
   * @see #getAncestor()
   * @return a new <code>Result</code>.
   */
  public Result newInstance() {
    return new Result(this, _stm, _ctx);
  }
  
  public String toString(){
    StringBuffer buf = new StringBuffer()
     .append("[state=").append(_currentState)
     .append(", err=").append(_err)
     .append(", ancestor=");
    
    if(_ancestor != null){
      buf.append(_ancestor._currentState);
    }
    else{
      buf.append("null");
    }
    
    buf.append(", handlingError=").append((_status & STATUS_HANDLING_ERROR) != 0)
      .append(", errorHandled=").append((_status & STATUS_HANDLED_ERROR) != 0)
      .append(", aborted=").append((_status & STATUS_ABORTED) != 0)
      .append(" ]");
     
    
    return buf.toString();
    
  }
}
