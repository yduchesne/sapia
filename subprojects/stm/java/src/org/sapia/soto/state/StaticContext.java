package org.sapia.soto.state;

import gnu.trove.THashMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sapia.soto.state.config.Globals;

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
public class StaticContext {
  protected THashMap     _states           = new THashMap();
  protected List         _listeners        = new ArrayList();
  protected Globals      _globals          = new Globals();
  protected THashMap     _modules          = new THashMap();
  protected ArrayList    _anonymousModules = new ArrayList();
  protected StateMachine _parent;
  protected boolean      _inheritModules, _inheritGlobals;
  protected String       _name;
  protected StatePath    _path;

  public StateMachine getParent() {
    return _parent;
  }

  void init() {
    initModules(_modules.values().iterator());
    initModules(_anonymousModules.iterator());
    _states.compact();
    _modules.compact();
    _anonymousModules.trimToSize();
  }

  Map getStates() {
    return _states;
  }
  
  /**
   * @return the array of names of the states that are part of this instance's
   * state machine.
   */
  public String[] getStateNames(){
    return (String[])_states.keySet().toArray(new String[_states.size()]);
  }
  
  /**
   * @return the <code>StatePath</code> corresponding to this instance's
   * state machine.
   */
  public StatePath getModulePath(){
    if(_path == null){
      ArrayList tokens = new ArrayList();
      doGetModulePath(tokens);
      StatePath path = new StatePath(tokens, true);
      _path = path;
      return _path.copy();
    }
    else{
      return _path.copy();
    }
  }
  
  protected void doGetModulePath(List tokens){
    if(_name != null){
      tokens.add(0, new StatePath.Token(_name));
    }
    if(_parent != null){
      _parent.getStmContext().doGetModulePath(tokens);
    }
  }
  
  StateHolder getStateFor(String stateId, boolean fromParent) {
    StateHolder sh = (StateHolder) _states.get(stateId);
    if(sh == null && fromParent){
      for(int i = 0; i < _anonymousModules.size(); i++) {
        Module m = (Module) _anonymousModules.get(i);
        sh = m.getStateMachine(true)._stmContext.getStateFor(stateId, fromParent);
        if(sh != null){
          break;
        }
      }
    }
    return sh;
  }
  
  StateMachine getStateMachineFor(String stateId, boolean fromParent){
    StateMachine stm = null;
    for(int i = 0; i < _anonymousModules.size(); i++) {
      Module m = (Module) _anonymousModules.get(i);
      StateMachine temp = m.getStateMachine(true);
      if(temp.getStmContext().getStateFor(stateId, fromParent) != null){
        stm = temp;
        break;
      }
    }
    if(stm == null && _inheritModules && _parent != null){
      if(_parent.getStmContext().getStateFor(stateId, false) != null){
        return _parent;
      }
      return _parent._stmContext.getStateMachineFor(stateId, false);
    }
    return stm;
  }

  /**
  boolean execute(Result rs) throws StateExecException{
    for(int i = 0; i < _anonymousModules.size(); i++) {
      Module m = (Module) _anonymousModules.get(i);
      StateMachine stm = m.getStateMachine(true);
      if(stm._stmContext.hasState(rs.getNextStateId())){
        stm.execute(rs.getNextStateId(), null, rs);
        return true;
      }
    }    
    return false;
  }  */

  List getStateListeners() {
    return _listeners;
  }

  Globals getGlobals() {
    return _globals;
  }

  Map getModules() {
    return _modules;
  }

  List getAnonymousModules() {
    return _anonymousModules;
  }

  void setParent(StateMachine parent) {
    _parent = parent;
  }
  
  void setName(String name){
    _name = name;
  }
  
  private void initModules(Iterator itr) {
    Module mod;

    while(itr.hasNext()) {
      mod = (Module) itr.next();

      if(mod.getStateMachine(false) == null) {
        throw new IllegalStateException(
            "State machine instance not specified for module: " + mod.getName());
      }

      if(mod.isInheritGlobals()
          && (mod.getStateMachine(false)._stmContext._globals != null)) {
        mod.getStateMachine(false)._stmContext._globals.setParent(_globals);
      }

      mod.getStateMachine(false)._stmContext._inheritModules = mod.isInheritModules();
      mod.getStateMachine(false)._stmContext._inheritGlobals = mod.isInheritGlobals();
      mod.getStateMachine(false).init();
    }
  }
  
  public String toString(){
    String parentInfo;
    if(_parent == null){
      parentInfo = "null";
    }
    else if(_parent._stmContext._parent == null){
      parentInfo = "root";      
    }
    else if(_parent._stmContext._name == null){
      parentInfo = "anonymous";
    }
    else{
      parentInfo = _parent._stmContext._name;
    }
    return new StringBuffer("[ module name=").append(_name)
     .append(", parent=").append(parentInfo)  
     .append(", states=").append(new ArrayList(_states.keySet()).toString())
     .append(" ]").toString();
  }
}
