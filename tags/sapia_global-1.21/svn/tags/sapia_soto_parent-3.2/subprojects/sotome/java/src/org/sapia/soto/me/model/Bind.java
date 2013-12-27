package org.sapia.soto.me.model;

import org.sapia.soto.me.ConfigurationException;
import org.sapia.soto.me.MIDletEnv;
import org.sapia.soto.me.MIDletEnvAware;
import org.sapia.soto.me.xml.NullObject;
import org.sapia.soto.me.xml.ObjectCreationCallback;

/**
 * This class implements the <code>soto:bind</code> tag for the SotoMe container.
 *
 * @author Jean-Cédric Desrochers
 */
public class Bind extends BaseModel implements MIDletEnvAware, ObjectCreationCallback {

  public static final String TAG_ID = "id";
  
  private MIDletEnv _env;
  
  /** The identifier of the object to bind. */
  private String _id;
  
  /** The object to bind. */
  private Object _object;

  /**
   * Creates a new Bind instance.
   */
  public Bind() {
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

  /**
   * Returns the object value.
   *
   * @return The object value.
   */
  public Object getObject() {
    return _object;
  }

  /**
   * Changes the value of the object.
   *
   * @param aObject The new object value.
   */
  public void setObject(Object aObject) {
    _object = aObject;
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
      setObject(anObject);
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    if (_id == null) {
      throw new ConfigurationException("The id attribute of the object to bind is null");
    }
    
    _env.bindObject(_id, _object);
    
    return NullObject.getInstance();
  }
}
