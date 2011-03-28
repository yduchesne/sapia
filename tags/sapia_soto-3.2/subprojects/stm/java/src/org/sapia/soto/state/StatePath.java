package org.sapia.soto.state;

import java.util.ArrayList;
import java.util.List;

/**
 * Models a path consisting of the fully-qualified-name of a state
 * to execute (including the module of which the state is part).
 * <p>
 * A the fully-qualifed-name of a state has the following format:
 * <pre>
 *  module_1.module_2.module_*.state_identifier
 * </pre>
 * For example:
 * <pre>
 *  users.accounts.upateAccount
 * </pre>
 * In the above, the "users" and "accounts" token represent module (where
 * "accounts" is a module contained in the "accounts" module). The 
 * "updateAccount" token corresponds to a state withing the "accounts"
 * module.
 * <p>
 * Note that a path can be delimited by "/" and/or "." characters. In addition,
 * note that an instance of this class is <b>not thread-safe</b>.
 * 
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
public class StatePath {
  
  public static final char SLASH_SEPARATOR = '/';  
  public static final String TOP_INDICATOR = "@";  

  private ArrayList        _tokens   = new ArrayList();
  private boolean          _absolute;
  private int              _index    = 0;
  private String           _stateId;
  
  protected StatePath(ArrayList tokens, boolean absolute){
    _absolute = true;
    _tokens = tokens;
  }

  public StatePath(String stateId) {
    _stateId = stateId;
  }
  
  public StatePath(String stateId, String module) {
    _stateId = stateId;
    if(module != null) {
      if(module.indexOf(SLASH_SEPARATOR) > -1) {
        StatePath sp = StatePath.parse(new StringBuffer(module).append(
            SLASH_SEPARATOR).append(stateId).toString());
        _tokens = sp._tokens;
        _stateId = sp._stateId;
        _absolute = sp._absolute;
      } else {
        _tokens.add(new Token(module));
      }
    }
  }

  private StatePath() {
  }
  
  /**
   * Resets the internal token counter. 
   */
  public void reset(){
    _index = 0;
  }

  /**
   * @return the state identifier that this instance holds - which corresponds
   *         to a state to execute in a state machine.
   */
  public String getStateId() {
    return _stateId;
  }

  /**
   * @return <code>true</code> if the path represented by this instance is an
   *         absolute path.
   */
  public boolean isAbsolute() {
    return _absolute;
  }
  
  /**
   * @param abs <code>true</code> if this instance corresponds to an absolute 
   * path.
   */
  public void setAbsolute(boolean abs){
    _absolute = abs;
  }

  /**
   * @return <code>true</code> if this instance has another path token
   *         pending.
   */
  public boolean hasNextPathToken() {
    return _index < _tokens.size();
  }

  /**
   * @return the next path token that this instance holds.
   */
  public String nextPathToken() {
    return ((Token) _tokens.get(_index++)).toString();
  }
  
  public boolean isNextParent(){
    return ((Token) _tokens.get(_index)).isParent;
  }

  /**
   * @param fullpath
   *          the full path to a module.
   * @return the <code>StatePath</code> corresponding to the given path.
   */
  public static StatePath parse(String fullpath) {
    char c;
    StringBuffer buf = new StringBuffer();
    StatePath path = new StatePath();
    int tokenCount = 0;
    for(int i = 0; i < fullpath.length(); i++) {
      c = fullpath.charAt(i);
      if(c == SLASH_SEPARATOR) {
        if(buf.length() == 0) {
          if(tokenCount == 0) {
            path._absolute = true;
            tokenCount++;
            continue;
          } else
            throw new IllegalArgumentException(
                "Path token cannot have zero length: " + fullpath);
        }
        path._tokens.add(new Token(buf.toString()));
        buf.delete(0, buf.length());
        tokenCount++;
      } else {
        buf.append(c);
      }
    }
    if(buf.length() > 0) {
      path._tokens.add(new Token(buf.toString()));
    }
    if(path._tokens.size() == 0) {
      throw new IllegalArgumentException("Path invalid: " + fullpath);
    }
    path._stateId = ((Token) path._tokens.remove(path._tokens.size() - 1)).toString();
    return path;
  }
  
  /**
   * Returns a copy of this instance. 
   * @return a <code>StatePath</code>.
   */
  public StatePath copy(){
    StatePath sp = new StatePath();
    sp._stateId = _stateId;
    sp._tokens = _tokens;
    sp._absolute = _absolute;
    sp._index = _index;
    return sp;
  }
  
  /**
   * Backs up to the previous token.
   */
  public void back(){
    if(_index > 0){
      _index--;
    }
  }
  
  /**
   * @return an unmodifiable <code>List</code> of this instance's tokens.
   */
  public List asList(){
    List tokens = new ArrayList(_tokens.size());
    for(int i = 0; i < _tokens.size(); i++){
      StatePath.Token token = (StatePath.Token)_tokens.get(i);
      tokens.add(token.token);
    }
    return tokens;
  }
  
  /**
   * Moves to the next token.
   */
  public void forward(){
    if(_index < _tokens.size()){
      _index++;
    }
  }  
  
  public boolean equals(Object other){
    if(other instanceof StatePath){
      StatePath path = (StatePath)other;
      if(_absolute == path._absolute && _tokens.size() == path._tokens.size()){
        for(int i = 0; i < _tokens.size(); i++){
          if(!_tokens.get(i).equals(path._tokens.get(i))){
            return false;
          }
        }
        return true;
      }
      return false;
    }
    return false;
  }
  
  public String toString() {
    StringBuffer buf = new StringBuffer();
    for(int i = 0; i < _tokens.size(); i++) {
      buf.append(_tokens.get(i));
      if(i < _tokens.size() - 1) {
        buf.append(SLASH_SEPARATOR);
      }
    }
    return buf.toString();
  }
  
  public String toPathString() {
    StringBuffer buf = new StringBuffer();
    for(int i = 0; i < _tokens.size(); i++) {
      buf.append(_tokens.get(i));
      if(i < _tokens.size() - 1) {
        buf.append(SLASH_SEPARATOR);
      }
    }
    return buf.toString();
  }
  
  /**
   * Tells if the token that will be returned by the <code>nextPathToken</code> method is the first token
   * @return
   */
  public boolean isFirstToken(){
    return _index==0;
  }
  
  public static final class Token{
    String token;
    boolean isParent;
    
    Token(String token){
      this.token = token;
      isParent = token.equals(TOP_INDICATOR);
    }

    public String toString(){
      return token;
    }    
    
    public boolean equals(Object o){
      return ((Token)o).token.equals(token);
    }
  }
  

}
