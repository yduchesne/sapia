package org.sapia.soto.log;

import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.NotFoundException;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 * This class represents a logger reference to permform the injection of a previously defined logger
 * of the log4j service. 
 *
 * @author Jean-Cedric Desrochers
 */
public class LoggerRef implements EnvAware, ObjectCreationCallback{

  public static final String TYPE_LOG4J = "log4j";
  public static final String TYPE_COMMONS = "commons";
  
  private Env _env;

  /** The type of logger reference to inject. */
  private String _type;
  
  /** The identifier of the referenced logger. */
  private String _id;
  
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
   * @return Returns the type.
   */
  public String getType() {
    return _type;
  }
  
  /**
   * @param aType The type to set.
   */
  public void setType(String aType) {
    _type = aType;
  }
  
  /* (non-Javadoc)
   * @see org.sapia.soto.EnvAware#setEnv(org.sapia.soto.Env)
   */
  public void setEnv(Env anEnv) {
    _env = anEnv;
  }

  /* (non-Javadoc)
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    if (_id == null) {
      throw new ConfigurationException("The id of this logger reference is null");
    } else if (_type == null) {
      throw new ConfigurationException("The type of this logger reference is null");
    }
    
    try {
      LogService service = (LogService) _env.lookup(LogService.class);
      
      LoggerDef def = (LoggerDef) service.getLoggerDef(_id);
      if (def == null) {
        throw new ConfigurationException("Error resolving logger reference '" + _id + "' - could not find the logger definition");
      }
      
      if (_type.equals(TYPE_LOG4J)) {
        return Logger.getLogger(def.getName());
      } else if (_type.equals(TYPE_COMMONS)) {
        return LogFactory.getLog(def.getName());
      } else {
        throw new ConfigurationException("The type of this logger reference is invalid: " + _type);
      }
      
    } catch (NotFoundException nfe) {
      throw new ConfigurationException("Error resolving logger reference '" + _id + "' - Could not find the Log4jService", nfe);
    }
  }
}
