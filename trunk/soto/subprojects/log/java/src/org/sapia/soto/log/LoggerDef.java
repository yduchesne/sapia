package org.sapia.soto.log;


import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 * This class defines a log4j logger reference for injection of loggers in services.  
 *
 * @author Jean-Cedric Desrochers
 */
public class LoggerDef implements ObjectCreationCallback {
  
  /** The internal id of this logger reference. */
  private String _id;
  
  /** The log4j logger name of this logger definition. */
  private String _name;

  /**
   * @return Returns the id.
   */
  public String getId() {
    return _id;
  }
  
  /**
   * @param aId The id to set.
   */
  public void setId(String aId) {
    _id = aId;
  }
  
  /**
   * @return Returns the name.
   */
  public String getName() {
    return _name;
  }
  
  /**
   * @param aName The name to set.
   */
  public void setName(String aName) {
    _name = aName;
  }

  /* (non-Javadoc)
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    if (_id == null) {
      throw new ConfigurationException("The id of the logger def is null");
    } else if (_name == null) {
      throw new ConfigurationException("The name of the logger def is null");
    }

    return this;
  }
}
