package org.sapia.soto.me.model;

import org.sapia.soto.me.ConfigurationException;
import org.sapia.soto.me.util.Log;
import org.sapia.soto.me.xml.NullObject;
import org.sapia.soto.me.xml.ObjectCreationCallback;

/**
 * This class implements the <code>soto:debug</code> tag for the SotoMe container.
 *
 * @author Jean-Cédric Desrochers
 */
public class DebugMessage extends BaseModel implements ObjectCreationCallback {

  public static final String TAG_MESSAGE = "message";
  public static final String TAG_MSG     = "msg";
  
  /** The message to display by this debug. */
  private String _message;

  /**
   * Creates a new DebugMessage instance.
   */
  public DebugMessage() {
  }
  
  /**
   * Returns the message value.
   *
   * @return The message value.
   */
  public String getMessage() {
    return _message;
  }

  /**
   * Changes the value of the message.
   *
   * @param aMessage The new message value.
   */
  public void setMessage(String aMessage) {
    _message = aMessage;
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectHandler#handleObject(java.lang.String, java.lang.Object)
   */
  public void handleObject(String aName, Object anObject) throws ConfigurationException {
    if (TAG_MESSAGE.equals(aName) || TAG_MSG.equals(aName)) {
      assertObjectType(String.class, aName, anObject);
      setMessage((String) anObject);

    } else {
      throwUnrecognizedObject(aName, anObject);
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    if (_message != null && Log.isDebug()) {
      Log.debug("MESSAGE: " + _message);
    }
    
    return NullObject.getInstance();
  }
}
