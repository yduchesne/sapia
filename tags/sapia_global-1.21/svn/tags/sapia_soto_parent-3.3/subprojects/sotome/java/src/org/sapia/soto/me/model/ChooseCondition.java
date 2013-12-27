package org.sapia.soto.me.model;

import org.sapia.soto.me.ConfigurationException;
import org.sapia.soto.me.MIDletEnv;
import org.sapia.soto.me.MIDletEnvAware;
import org.sapia.soto.me.util.Log;
import org.sapia.soto.me.xml.J2meProcessingException;
import org.sapia.soto.me.xml.NullObject;
import org.sapia.soto.me.xml.ObjectCreationCallback;
import org.sapia.soto.me.xml.ObjectCreationException;
import org.sapia.soto.me.xml.ObjectFactory;

/**
 * This class implements the <code>soto:choose</code> tag for the SotoMe container.
 *
 * @author Jean-Cédric Desrochers
 */
public class ChooseCondition extends BaseModel implements MIDletEnvAware, ObjectFactory, ObjectCreationCallback {

  public static final String TAG_WHEN      = "when";
  public static final String TAG_OTHERWISE = "otherwise";
  
  private static final String MODULE_NAME = "Choose";

  /** The SotoMe container environment. */
  private MIDletEnv _env;
  
  /** Indicates if at least one predicate of this choose condition was executed. */
  private boolean _isConditionExecuted;
  
  /** The result object of this choose condition. */
  private Object _resultObject;
  
  /**
   * Creates a new ChooseCondition instance.
   */
  public ChooseCondition() {
    _isConditionExecuted = false;
  }
  
  /**
   * Returns true if at least one predicate of this choose condition was executed.
   * 
   * @return True if at least one predicate of this choose condition was executed
   */
  protected boolean isConditionExecuted() {
    return _isConditionExecuted;
  }
  
  /**
   * Changes the is condition executed indicator.
   * 
   * @param aValue The new value.
   */
  protected void setConditionExecuted(boolean aValue) {
    _isConditionExecuted = aValue;
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectFactory#newObjectFor(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
   */
  public Object newObjectFor(String aPrefix, String aNamespaceURI, String anElementName, Object aParent) throws ObjectCreationException {
    if (TAG_WHEN.equals(anElementName)) {
      WhenCondition whenCondition = new WhenCondition();
      whenCondition.setMIDletEnv(_env);
      return whenCondition;

    } else if (TAG_OTHERWISE.equals(anElementName)) {
      OtherwiseCondition otherwiseCondition = new OtherwiseCondition();
      otherwiseCondition.setMIDletEnv(_env);
      return otherwiseCondition;

    } else {
      throw new ObjectCreationException("Could not create an object for element '" + anElementName + "'");
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectHandler#handleObject(java.lang.String, java.lang.Object)
   */
  public void handleObject(String aName, Object anObject) throws ConfigurationException {
    _resultObject = anObject;
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    if (_resultObject != null) {
      return _resultObject;
    } else {
      return NullObject.getInstance();
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.MIDletEnvAware#setMIDletEnv(org.sapia.soto.me.MIDletEnv)
   */
  public void setMIDletEnv(MIDletEnv aMIDletEnv) {
    _env= aMIDletEnv;
  }

  /**
   * Inner class that implements the <soto:when> tag 
   *
   * @author Jean-Cédric Desrochers
   */
  public class WhenCondition extends IfCondition {
    public WhenCondition() {
    }

    /* (non-Javadoc)
     * @see org.sapia.soto.me.model.IfCondition#evaluationExpression()
     */
    protected boolean evaluationExpression() {
      return !isConditionExecuted() && super.evaluationExpression();
    }

    /* (non-Javadoc)
     * @see org.sapia.soto.me.xml.XmlConsumer#consumeXml(org.sapia.soto.me.xml.XmlConsumer.XmlCursor)
     */
    public void consumeXml(XmlCursor aCursor) throws J2meProcessingException {
      if (evaluationExpression()) {
        if (Log.isDebug()) {
          Log.debug(MODULE_NAME, "WHEN condition (predicate not executed && " + getParameter() + " == " + getExpectedValue()+ ") meet... processing XML");
        }
        setResultObject(aCursor.processXmlElement(null));
        setConditionExecuted(true);
      } else {
        if (Log.isDebug()) {
          Log.debug(MODULE_NAME, "WHEN condition (predicate already executed || " + getParameter() + " != " + getExpectedValue() + ") not meet... skipping XML");
        }
        aCursor.skipXmlElement();
      }
    }
  }

  /**
   * Inner class that implements the <soto:otherwise> tag 
   *
   * @author Jean-Cédric Desrochers
   */
  public class OtherwiseCondition extends IfCondition {
    public OtherwiseCondition() {
    }
    
    /* (non-Javadoc)
     * @see org.sapia.soto.me.model.IfCondition#evaluationExpression()
     */
    protected boolean evaluationExpression() {
      return !isConditionExecuted();
    }

    /* (non-Javadoc)
     * @see org.sapia.soto.me.xml.XmlConsumer#consumeXml(org.sapia.soto.me.xml.XmlConsumer.XmlCursor)
     */
    public void consumeXml(XmlCursor aCursor) throws J2meProcessingException {
      if (evaluationExpression()) {
        if (Log.isDebug()) {
          Log.debug(MODULE_NAME, "OTHERWISE condition (predicate not executed) meet... processing XML");
        }
        setResultObject(aCursor.processXmlElement(null));
        setConditionExecuted(true);
      } else {
        if (Log.isDebug()) {
          Log.debug(MODULE_NAME, "OTHERWISE condition (predicate already executed) not meet... skipping XML");
        }
        aCursor.skipXmlElement();
      }
    }
  }
}
