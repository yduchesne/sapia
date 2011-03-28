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
 * This class implements the <code>soto:switch</code> tag for the SotoMe container.
 *
 * @author Jean-Cédric Desrochers
 */
public class SwitchCondition extends BaseModel implements MIDletEnvAware, ObjectFactory, ObjectCreationCallback {

  public static final String TAG_PARAM   = "param";
  public static final String TAG_CASE    = "case";
  public static final String TAG_DEFAULT = "default";

  private static final String MODULE_NAME = "Switch";
  
  /** The SotoMe container environment. */
  private MIDletEnv _env;
  
  /** The parameter to be evaluated by this switch confition. */
  private String _parameter;
  
  /** Indicates if at least one predicate of this choose condition was executed. */
  private boolean _isConditionExecuted;
  
  /** The result object of this choose condition. */
  private Object _resultObject;
  
  /**
   * Creates a new SwitchCondition instance.
   */
  public SwitchCondition() {
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

  /**
   * Returns the parameter value.
   *
   * @return The parameter value.
   */
  public String getParameter() {
    return _parameter;
  }

  /**
   * Changes the value of the parameter.
   *
   * @param aParameter The new parameter value.
   */
  public void setParameter(String aParameter) {
    _parameter = aParameter;
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectFactory#newObjectFor(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
   */
  public Object newObjectFor(String aPrefix, String aNamespaceURI, String anElementName, Object aParent) throws ObjectCreationException {
    if (TAG_CASE.equals(anElementName)) {
      CaseCondition caseCondition = new CaseCondition();
      caseCondition.setMIDletEnv(_env);
      caseCondition.setParameter(_parameter);
      return caseCondition;

    } else if (TAG_DEFAULT.equals(anElementName)) {
      DefaultCondition defaultCondition = new DefaultCondition();
      defaultCondition.setMIDletEnv(_env);
      return defaultCondition;

    } else {
      throw new ObjectCreationException("Could not create an object for element '" + anElementName + "'");
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectHandler#handleObject(java.lang.String, java.lang.Object)
   */
  public void handleObject(String aName, Object anObject) throws ConfigurationException {
    if (TAG_PARAM.equals(aName)) {
      assertObjectType(String.class, aName, anObject);
      setParameter((String) anObject);
    } else {
      _resultObject = anObject;
    }
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
   * Inner class that implements the <soto:case> tag 
   *
   * @author Jean-Cédric Desrochers
   */
  public class CaseCondition extends IfCondition {
    public CaseCondition() {
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
          Log.debug(MODULE_NAME, "CASE condition (predicate not executed && " + this.getParameter() + " == " + getExpectedValue()+ ") meet... processing XML");
        }
        setResultObject(aCursor.processXmlElement(null));
        setConditionExecuted(true);
      } else {
        if (Log.isDebug()) {
          Log.debug(MODULE_NAME, "CASE condition (predicate already executed || " + this.getParameter() + " != " + getExpectedValue() + ") not meet... skipping XML");
        }
        aCursor.skipXmlElement();
      }
    }
  }

  /**
   * Inner class that implements the <soto:default> tag 
   *
   * @author Jean-Cédric Desrochers
   */
  public class DefaultCondition extends IfCondition {
    public DefaultCondition() {
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
          Log.debug(MODULE_NAME, "DEFAULT condition (predicate not executed) meet... processing XML");
        }
        setResultObject(aCursor.processXmlElement(null));
        setConditionExecuted(true);
      } else {
        if (Log.isDebug()) {
          Log.debug(MODULE_NAME, "DEFAULT condition (predicate already executed) not meet... skipping XML");
        }
        aCursor.skipXmlElement();
      }
    }
  }
}
