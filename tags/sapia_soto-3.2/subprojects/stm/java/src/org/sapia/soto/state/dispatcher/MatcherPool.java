package org.sapia.soto.state.dispatcher;

import java.util.LinkedList;
import org.sapia.soto.state.Context;
import org.sapia.soto.state.StateMachineService;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;

/**
 * Pools <code>Matcher</code> instances.
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
public class MatcherPool implements ObjectHandlerIF {

  private LinkedList          _pool = new LinkedList();

  private String              _pattern;
  private String              _state;
  private StateMachineService _stm;

  /**
   * Matches a state against a given URI; if there is a match, triggers the
   * state's execution (this method in fact delegates it matching work to a
   * pooled <code>Matcher</code> instance that it keeps internally).
   * 
   * @see Matcher
   * 
   * @param uri
   *          a URI to match.
   * @param ctx
   *          a <code>Context</code>
   * @return return <code>true</code> if the given path has been matched.
   * @throws Exception
   *           if a problem occurs.
   */
  public boolean matches(String uri, Context ctx) throws Exception {
    Matcher matcher = null;
    try {
      matcher = acquire();
      return matcher.matches(uri, ctx);
    } finally {
      if(matcher != null) {
        release(matcher);
      }
    }
  }


  public void setTarget(String state) {
    _state = state;
  }

  public void setPattern(String pattern) {
    _pattern = pattern;
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(java.lang.String,
   *      java.lang.Object)
   */
  public void handleObject(String name, Object stm)
      throws ConfigurationException {
    if(stm instanceof StateMachineService) {
      if(_stm != null) {
        throw new ConfigurationException(
            "State machine already specified for matcher");
      }
      _stm = (StateMachineService) stm;
    } else {
      throw new ConfigurationException("Expecting instance of "
          + StateMachineService.class.getName() + "; got: "
          + stm.getClass().getName() + " for: " + name);
    }
  }

  private synchronized Matcher acquire() {
    if(_pool.size() == 0) {
      Matcher matcher = new Matcher();
      if(_state != null) {
        matcher.setTarget(_state);
      }
      if(_pattern != null) {
        matcher.setPattern(_pattern);
      }
      matcher.setStateMachine(_stm);
      return matcher;

    } else {
      return (Matcher) _pool.removeFirst();
    }
  }

  private synchronized void release(Matcher matcher) {
    _pool.add(matcher);
  }
}
