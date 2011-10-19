package org.sapia.soto.me.model;

import org.sapia.soto.me.ConfigurationException;
import org.sapia.soto.me.MIDletEnv;
import org.sapia.soto.me.MIDletEnvAware;
import org.sapia.soto.me.util.Log;
import org.sapia.soto.me.xml.J2meProcessingException;
import org.sapia.soto.me.xml.NullObject;
import org.sapia.soto.me.xml.ObjectCreationCallback;
import org.sapia.soto.me.xml.XmlConsumer;

/**
 * This class implements the <code>soto:if</code> tag for the SotoMe container.
 *
 * @author Jean-Cédric Desrochers
 */
public class IfCondition extends BaseModel implements MIDletEnvAware, XmlConsumer, ObjectCreationCallback {

  public static final String TAG_PARAM  = "param";
  public static final String TAG_EQUALS = "equals";
  
  private MIDletEnv _env;
  
  /** The parameter to be evaluated by this condition. */
  private String _parameter;
  
  /** The expected value of this if condition. */
  private String _expectedValue;
  
  /** The result object of the this conditional processing. */ 
  private Object _resultObject;

  /**
   * Creates a new IfCondition instance.
   */
  public IfCondition() {
  }
  
  /**
   * Returns the expectedValue value.
   *
   * @return The expectedValue value.
   */
  public String getExpectedValue() {
    return _expectedValue;
  }

  /**
   * Changes the value of the expectedValue.
   *
   * @param aExpectedValue The new expectedValue value.
   */
  public void setExpectedValue(String aExpectedValue) {
    _expectedValue = aExpectedValue;
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
  
  /**
   * This method evaluates this logic expression and return true if it needs to
   * be executed.
   *  
   * @return True if this condition's expression should be executed, false otherwise.
   */
  protected boolean evaluationExpression() {
    String actualValue = _env.getProperty(_parameter);
    
    return (_expectedValue == null && actualValue == null) ||
           (_expectedValue != null && _expectedValue.equals(actualValue));
  }

  /**
   * Change the result object of this condition.
   * 
   * @param anObject The new result object.
   */
  protected void setResultObject(Object anObject) {
    _resultObject = anObject;
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectHandler#handleObject(java.lang.String, java.lang.Object)
   */
  public void handleObject(String aName, Object anObject) throws ConfigurationException {
    if (TAG_PARAM.equals(aName)) {
      assertObjectType(String.class, aName, anObject);
      setParameter((String) anObject);

    } else if (TAG_EQUALS.equals(aName)) {
      assertObjectType(String.class, aName, anObject);
      setExpectedValue((String) anObject);
      
    } else {
      throwUnrecognizedObject(aName, anObject);
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.XmlConsumer#consumeXml(org.sapia.soto.me.xml.XmlConsumer.XmlCursor)
   */
  public void consumeXml(XmlCursor aCursor) throws J2meProcessingException {
    if (evaluationExpression()) {
      if (Log.isDebug()) {
        Log.debug("IF condition (" + _parameter + " == " + _expectedValue + ") meet... processing XML");
      }
      _resultObject = aCursor.processXmlElement(null);
    } else {
      if (Log.isDebug()) {
        Log.debug("IF condition (" + _parameter + " != " + _expectedValue + ") not meet... skipping XML");
      }
      aCursor.skipXmlElement();
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
}
