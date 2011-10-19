/**
 * 
 */
package org.sapia.soto.me;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.sapia.soto.me.util.CompositeRuntimeException;
import org.sapia.soto.me.util.Log;
import org.sapia.soto.me.util.TimeSpan;

/**
 * This class is a MIDlet implementation that will contain a Soto container designed
 * with limited features for J2ME.
 *
 * @author Jean-Cédric Desrochers
 */
public class SotoMIDlet extends MIDlet {

  /** Defines the property name that defines the soto config resource to load. */   
  public static final String SOTO_CONFIG_RESOURCE = "soto.config.resource";
  
  /** The SotoMe container that will support this MIDlet. */
  private SotoMeContainer _sotoMe;
  
  /** The static application loader of this SotoMIDlet. */
  private SotoMeStaticLoader _staticLoader;
  
  /**
   * Creates a new SotoMIDlet instance.
   */
  public SotoMIDlet() {
    _sotoMe = new SotoMeContainer(this);
  }
  
  /**
   * Changes the static application loader of this Soto MIDlet.
   * 
   * @param aStaticLoader The new loader.
   */
  public void setStaticLoader(SotoMeStaticLoader aStaticLoader) {
    _staticLoader = aStaticLoader;
  }
  
  /* (non-Javadoc)
   * @see javax.microedition.midlet.MIDlet#startApp()
   */
  protected void startApp() throws MIDletStateChangeException {
    
    // Load and start the SotoMe container if it's first time
    if (_sotoMe.getLifeCycle().isCreated()) {
      if (_staticLoader != null) {
        try {
          TimeSpan timeSpan = new TimeSpan().start();
          _staticLoader.loadApplication(_sotoMe);
          
          timeSpan.stop();
          Log.info("SotoMIDlet", "Loaded container with static application  [completed in " + timeSpan.getDeltaMillis() + " ms]\n=====> SotoMe LOADED <=====");
          _sotoMe.getLifeCycle().loaded();
          
        } catch (Exception e) {
          throw new CompositeRuntimeException("Could not start SotoMIDlet - error occured loading SotoMe container from static loader", e);
        }
          
      } else {
        String configResource = getAppProperty(SOTO_CONFIG_RESOURCE);
        if (configResource == null) {
          throw new IllegalArgumentException("Could not start SotoMIDlet - no configuration resource found for property " + SOTO_CONFIG_RESOURCE); 
        }
        try {
          _sotoMe.load(configResource);
        } catch (Exception e) {
          throw new CompositeRuntimeException("Could not start SotoMIDlet - error occured loading SotoMe container from resource " + configResource, e);
        }
      }
    }
    
    // Manage action according to SotoMe container state 
    if (_sotoMe.getLifeCycle().isPaused() || _sotoMe.getLifeCycle().isLoaded()) {
      try {
        _sotoMe.start();
      } catch (Exception e) {
        throw new CompositeRuntimeException("Could not start SotoMIDlet - error occured starting SotoMe container", e);
      }
    } else {
      throw new IllegalStateException("The startApp() action on the SotoMIDlet could not process - the state of the SotoMe container is invalid: "
              + _sotoMe.getLifeCycle());
    }
  }

  /* (non-Javadoc)
   * @see javax.microedition.midlet.MIDlet#pauseApp()
   */
  protected void pauseApp() {
    // Manage action according to SotoMe container state 
    if (_sotoMe.getLifeCycle().isStarted()) {
      try {
        _sotoMe.pause();
      } catch (Exception e) {
        throw new CompositeRuntimeException("Could not pause SotoMIDlet - error occured pausing SotoMe container", e);
      }
    } else {
      throw new IllegalStateException("The pauseApp() action on the SotoMIDlet could not process - the state of the SotoMe container is invalid: "
              + _sotoMe.getLifeCycle());
    }
  }

  /* (non-Javadoc)
   * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
   */
  protected void destroyApp(boolean isUnconditional) throws MIDletStateChangeException {
    try {
      _sotoMe.dispose();
    } catch (Exception e) {
      throw new CompositeRuntimeException("Could not destroy SotoMIDlet - error occured disposing SotoMe container", e);
    }
  }

}
