package org.sapia.soto.state.dispatcher;

import gnu.trove.THashMap;

import org.sapia.soto.state.Context;
import org.sapia.soto.state.StateMachineService;
import org.sapia.soto.state.StatePath;
import org.sapia.soto.util.matcher.UriPattern;
import org.sapia.util.text.MapContext;
import org.sapia.util.text.SystemContext;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.text.TemplateElementIF;
import org.sapia.util.text.TemplateFactory;

/**
 * An instance of this class matches paths to states. The matched state for a
 * given path is executed by the <code>StateMachineService</code> instance
 * that this class encapsulates.
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
public class Matcher {

  private static final String START_DELIM = "{";
  private static final String END_DELIM   = "}";

  private UriPattern          _pattern;
  private TemplateElementIF   _state;
  private TemplateFactory     _fac        = new TemplateFactory(START_DELIM,
                                              END_DELIM);
  private StateMachineService _stateMachine;
  private THashMap            _cache      = new THashMap();

  public Matcher() {
  }

  /**
   * @param patternString
   *          the pattern corresponding to the URIs that this instance should
   *          match.
   */
  public void setPattern(String patternString) {
    _pattern = UriPattern.parse(patternString);
  }

  /**
   * @param state
   *          the state that should be executed provided a match occurs.
   */
  void setTarget(String state) {
    _state = _fac.parse(state);
  }

  /**
   * @param svc
   *          the <code>StateMachineService</code> instance to which request
   *          are delegated.
   */
  void setStateMachine(StateMachineService svc) {
    _stateMachine = svc;
  }

  /**
   * @param uri
   *          a URI to match.
   * @param context
   *          a <code>Context</code>
   * @return <code>true</code> if the URI was matched.
   * @throws Exception
   *           if a problem occurs while performing the match operation.
   */
  public boolean matches(String uri, Context context) throws Exception {
    if(_state == null) {
      throw new IllegalStateException(
          "Matcher not initialized properly: state not specified");
    }
    if(_stateMachine == null) {
      throw new IllegalStateException(
          "Matcher not initialized properly: state machine not specified");
    }
    if(_pattern == null) {
      throw new IllegalStateException(
          "Matcher not initialized properly: URI pattern not specified");
    }

    StatePath path = (StatePath) _cache.get(uri);
    if(path != null) {
      path.reset();
      _stateMachine.execute(path, context);
      return true;
    }
    
    UriPattern.MatchResult res = _pattern.matchResult(uri);
    if(res.matched) {
      TemplateContextIF templateCtx = new MapContext(res.result,
          new SystemContext(), false);
      String state = _state.render(templateCtx);
      path = StatePath.parse(state);
      _cache.put(uri, path);
      _cache.compact();
      _stateMachine.execute(path, context);
      return true;
    } else {
      return false;
    }
  }

}
