package org.sapia.soto.state;

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
public class StateHolder {
  private boolean _visible = true;
  private State   _state;

  StateHolder(State state) {
    _state = state;
  }

  /**
   * @return <code>true</code> if the encapsulated <code>State</code> is
   *         visible - i.e.: can be called from outside the state machine.
   */
  public boolean isVisible() {
    return _visible;
  }

  /**
   * @return the <code>State</code> that this instance holds.
   */
  public State getState() {
    return _state;
  }

  /**
   * Sets this instance's state.
   * 
   * @param state
   *          a <code>State</code>.
   */
  void setState(State state) {
    _state = state;
  }

  /**
   * @param visible
   *          <code></code>
   */
  void setVisible(boolean visible) {
    _visible = visible;
  }
}
