package org.sapia.soto.aop;

import org.sapia.soto.ConfigurationException;

import org.sapia.util.xml.confix.ObjectHandlerIF;

/**
 * Models an advice definition.
 * 
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2003 <a
 *         href="http://www.sapia-oss.org">Sapia Open Source Software </a>. All
 *         Rights Reserved.</dd>
 *         </dt>
 *         <dt><b>License: </b>
 *         <dd>Read the license.txt file of the jar or visit the <a
 *         href="http://www.sapia-oss.org/license.html">license page </a> at the
 *         Sapia OSS web site</dd>
 *         </dt>
 *         </dl>
 */
public class AdviceDef implements ObjectHandlerIF {
  private String _clazz;
  private String _id;
  private Advice _instance;

  /**
   * Constructor for AdviceDef.
   */
  public AdviceDef() {
    super();
  }

  /**
   * Sets the class name of the defined advice.
   * 
   * @param clazz
   *          a class name.
   */
  public void setClass(String clazz) {
    _clazz = clazz;
  }

  /**
   * Sets the identifier of the defined advice.
   * 
   * @param id
   */
  public void setId(String id) {
    _id = id;
  }

  /*****************************************************************************
   * Returns the identifier of the defined advice.
   * 
   * @return the identifier of the defined advice.
   */
  public String getId() {
    return _id;
  }

  /**
   * Returns an advice instance corresponding to this definition.
   * 
   * @return an <code>Advice</code>.
   */
  public Advice getInstance() throws ConfigurationException {
    if(_instance != null) {
      return _instance;
    }

    if(_clazz == null) {
      if(_id != null) {
        throw new ConfigurationException(
            "'class' attribute not specified on advice definition");
      } else {
        throw new ConfigurationException(
            "'class' attribute not specified on advice definition: " + _id);
      }
    }

    try {
      return _instance = (Advice) Class.forName(_clazz).newInstance();
    } catch(ClassNotFoundException e) {
      throw new ConfigurationException("Could not find advice class", e);
    } catch(IllegalAccessException e) {
      throw new ConfigurationException(
          "Could not access constructor for security reasons; does the advice class "
              + _clazz + " has a public constructor?", e);
    } catch(InstantiationException e) {
      throw new ConfigurationException("Could not instantiate advice class", e);
    }
  }

  public void setAdvice(Advice advice)
      throws org.sapia.util.xml.confix.ConfigurationException {
    if(_instance == null) {
      _instance = (Advice) advice;
    } else {
      throw new org.sapia.util.xml.confix.ConfigurationException(
          "Advice instance already specified");
    }

    _instance = advice;
  }

  /**
   * @see ObjectHandlerIF#handleObject(java.lang.String, java.lang.Object)
   */
  public void handleObject(String name, Object obj)
      throws org.sapia.util.xml.confix.ConfigurationException {
    if(obj instanceof Advice) {
      setAdvice((Advice) obj);
    } else {
      throw new org.sapia.util.xml.confix.ConfigurationException(
          "element 'name' does not correspond to and Advice instance");
    }
  }
}
