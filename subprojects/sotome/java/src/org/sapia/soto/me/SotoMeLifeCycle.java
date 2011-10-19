/**
 * 
 */
package org.sapia.soto.me;

import org.sapia.soto.me.util.Log;

/**
 * This class defines the lyfecycle of the SotoMe container.
 * 
 * @author Jean-Cédric Desrochers
 */
public class SotoMeLifeCycle {
  
  public static int STATE_CREATED = 0;
  public static int STATE_LOADED = 1;
  public static int STATE_INITIALIZED = 2;
  public static int STATE_STARTED = 3;
  public static int STATE_PAUSED = 4;
  public static int STATE_DISPOSED = 5;
  public static String[] STATES = new String[] {
    "CREATED", "LOADED", "INITIALIZED", "STARTED", "PAUSED", "DISPOSED"
  };
  
  private static final String MODULE_NAME = "LifeCycle";
  
  /**
   * Utility method that returns a string description of the SotoMe container state passed in.
   * 
   * @param aValue The state value for which to get a description.
   * @return The description of the state.
   */
  public static String getStateDescription(int aValue) {
    if (aValue >= 0 && aValue < STATES.length) {
      return STATES[aValue];
    } else {
      throw new IllegalArgumentException("Could not return a description of the state - the value is invalid " + aValue);
    }
  }
  
  /** The state value of this life cycle object. */
  private int _state;
  
  /**
   * Creates a new SotoMeLifeCycle instance.
   */
  public SotoMeLifeCycle() {
    _state = STATE_CREATED;
  }

  /**
   * Returns the state of this lifecycle object.
   * 
   * @return The state of this lifecycle object.
   */
  public int getState() {
    return _state;
  }

  /**
   * Returns true if the state of this lifecycle is created, false otherwise.
   * 
   * @return True if the state of this lifecycle is created, false otherwise.
   */
  public boolean isCreated() {
    return _state == STATE_CREATED;
  }
  
  /**
   * Changes the state of this lifecycle object to {@link #STATE_LOADED}.
   */
  public void loaded() {
    _state = STATE_LOADED;
  }
  
  /**
   * Returns true if the state of this lifecycle is loaded, false otherwise.
   * 
   * @return True if the state of this lifecycle is loaded, false otherwise.
   */
  public boolean isLoaded() {
    return _state == STATE_LOADED;
  }
  
  /**
   * Changes the state of this lifecycle object to {@link #STATE_INITIALIZED}.
   */
  public void initialized() {
    _state = STATE_INITIALIZED;
  }
  
  /**
   * Returns true if the state of this lifecycle is initialized, false otherwise.
   * 
   * @return True if the state of this lifecycle is initialized, false otherwise.
   */
  public boolean isInitialized() {
    return _state == STATE_INITIALIZED;
  }
  
  /**
   * Changes the state of this lifecycle object to {@link #STATE_STARTED}.
   */
  public void started() {
    _state = STATE_STARTED;
  }
  
  /**
   * Returns true if the state of this lifecycle is started, false otherwise.
   * 
   * @return True if the state of this lifecycle is started, false otherwise.
   */
  public boolean isStarted() {
    return _state == STATE_STARTED;
  }
  
  /**
   * Changes the state of this lifecycle object to {@link #STATE_PAUSED}.
   */
  public void paused() {
    _state = STATE_PAUSED;
  }
  
  /**
   * Returns true if the state of this lifecycle is paused, false otherwise.
   * 
   * @return True if the state of this lifecycle is paused, false otherwise.
   */
  public boolean isPaused() {
    return _state == STATE_PAUSED;
  }
  
  /**
   * Changes the state of this lifecycle object to {@link #STATE_DISPOSED}.
   */
  public void dispose() {
    _state = STATE_DISPOSED;
  }
  
  /**
   * Returns true if the state of this lifecycle is disposed, false otherwise.
   * 
   * @return True if the state of this lifecycle is disposed, false otherwise.
   */
  public boolean isDisposed() {
    return _state == STATE_DISPOSED;
  }
  
  /**
   * Utility method to assert the current state of this SotoMe container.
   *  
   * @param anExpectedState The expected state.
   * @param anErrorDescription The error description if the assertion fails.
   * @exception IllegalStateException If the assertion fails.
   */
  protected void assertState(int anExpectedState, String anErrorDescription) throws IllegalStateException {
    if (_state != anExpectedState) {
      String message =  anErrorDescription + " - expected state is " + STATES[anExpectedState] + " but was " + STATES[_state];
      Log.error(MODULE_NAME, message);
      throw new IllegalStateException(message);
    }
  }

  /**
   * Utility method to assert the current state of this SotoMe container to a list of states.
   *  
   * @param anExpectedStates1 The first expected states.
   * @param anExpectedStates2 The second expected states.
   * @param anErrorDescription The error description if the assertion fails.
   * @exception IllegalStateException If the assertion fails.
   */
  protected void assertState(int anExpectedState1, int anExpectedState2, String anErrorDescription) throws IllegalStateException {
    if (_state != anExpectedState1 && _state != anExpectedState2) {
      String message = anErrorDescription + " - expected states are " + STATES[anExpectedState1] +
                       " or " + STATES[anExpectedState2] + ", but was " + STATES[_state];
      Log.error(MODULE_NAME, message);
      throw new IllegalStateException(message);
    }
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object anObject) {
    if (anObject == null) {
      return false;
    } else if (anObject instanceof SotoMeLifeCycle) {
      return _state == ((SotoMeLifeCycle) anObject)._state;
    } else if (anObject instanceof Integer) {
      return _state == ((Integer) anObject).intValue();
    } else if (anObject instanceof Long) {
      return _state == ((Long) anObject).longValue();
    } else {
      return false;
    }
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return getStateDescription(_state);
  }
}
