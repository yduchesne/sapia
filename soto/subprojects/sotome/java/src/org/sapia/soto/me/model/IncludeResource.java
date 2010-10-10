package org.sapia.soto.me.model;

import javolution.util.FastMap;

import org.sapia.soto.me.ConfigurationException;
import org.sapia.soto.me.MIDletEnv;
import org.sapia.soto.me.MIDletEnvAware;
import org.sapia.soto.me.xml.ObjectCreationCallback;

/**
 * This class implements the <code>soto:include</code> tag for the SotoMe container.
 * 
 * @author Jean-Cédric Desrochers
 */
public class IncludeResource extends BaseModel implements MIDletEnvAware, ObjectCreationCallback {

  public static final String TAG_URI = "uri";
  public static final String TAG_PARAM = "param";
  
  /** The SotoMe container enviroment context. */
  private MIDletEnv _env;
  
  /** The resource URI to include. */
  private String _resourceUri;
  
  /** The properties of this include. */
  private FastMap _properties;
  
  /**
   * Creates a new IncludeResource instance.
   */
  public IncludeResource() {
    _properties = new FastMap();
  }
  
  /**
   * Returns the resourceUri value.
   *
   * @return The resourceUri value.
   */
  public String getUri() {
    return _resourceUri;
  }

  /**
   * Changes the value of the resourceUri.
   *
   * @param aResourceUri The new resourceUri value.
   */
  public void setUri(String aResourceUri) {
    _resourceUri = aResourceUri;
  }

  /**
   * Returns the properties value.
   *
   * @return The properties value.
   */
  public FastMap getProperties() {
    return _properties;
  }

  /**
   * Changes the value of the properties.
   *
   * @param aProperties The new properties value.
   */
  public void setProperties(FastMap aProperties) {
    _properties = aProperties;
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectHandler#handleObject(java.lang.String, java.lang.Object)
   */
  public void handleObject(String aName, Object anObject) throws ConfigurationException {
    if (TAG_URI.equals(aName)) {
      assertObjectType(String.class, aName, anObject);
      setUri((String) anObject);
      
    } else if (anObject instanceof Parameter) {
      Parameter param = (Parameter) anObject;
      _properties.put(param.getName(), param.getValue());
      
    } else {
      throwUnrecognizedObject(aName, anObject);
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.MIDletEnvAware#setMIDletEnv(org.sapia.soto.me.MIDletEnv)
   */
  public void setMIDletEnv(MIDletEnv aMIDletEnv) {
    _env = aMIDletEnv;
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    try {
      return _env.include(_env.resolveResource(_resourceUri), _properties);
      
    } catch (Exception e) {
      throw new ConfigurationException("", e);
    }
  }
}
