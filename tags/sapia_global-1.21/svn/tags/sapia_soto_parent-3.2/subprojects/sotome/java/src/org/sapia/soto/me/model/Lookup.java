package org.sapia.soto.me.model;

import org.sapia.soto.me.ConfigurationException;
import org.sapia.soto.me.NotFoundException;
import org.sapia.soto.me.MIDletEnv;
import org.sapia.soto.me.MIDletEnvAware;
import org.sapia.soto.me.xml.ObjectCreationCallback;

/**
 * This class implements the <code>soto:lookup</code> tag for the SotoMe container.
 *
 * @author Jean-Cédric Desrochers
 */
public class Lookup extends BaseModel implements MIDletEnvAware, ObjectCreationCallback {

  public static final String TAG_ID = "id";
  
  private MIDletEnv _env;
  
  /** The identifier of the object to lookup. */
  private String _id;

  /**
   * Creates a new Lookup instance.
   */
  public Lookup() {
  }
  
  /**
   * Returns the id value.
   *
   * @return The id value.
   */
  public String getId() {
    return _id;
  }

  /**
   * Changes the value of the id.
   *
   * @param anId The new id value.
   */
  public void setId(String anId) {
    _id = anId;
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.MIDletEnvAware#setMIDletEnv(org.sapia.soto.me.MIDletEnv)
   */
  public void setMIDletEnv(MIDletEnv aMIDletEnv) {
    _env = aMIDletEnv;
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectHandler#handleObject(java.lang.String, java.lang.Object)
   */
  public void handleObject(String aName, Object anObject) throws ConfigurationException {
    if (TAG_ID.equals(aName)) {
      assertObjectType(String.class, aName, anObject);
      setId((String) anObject);
      
    } else {
      throwUnrecognizedObject(aName, anObject);
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    if (_id == null) {
      throw new ConfigurationException("The id attribute of the object to bind is null");
    }
    
    try {
      return _env.lookupObject(_id);
    } catch (NotFoundException nfe) {
      throw new ConfigurationException("No object found for the id: " + _id);
    }
  }
}
