package org.sapia.soto;

import org.sapia.soto.config.NullObjectImpl;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 * An instance of this class represents a <code>Service</code> attribute. Attributes
 * are specified as part of a service configuration. They allow adding runtime metadata
 * to services in a non-programmatic way, through configuration, in a manner similar
 * to JMX attributes.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class Attribute implements ObjectCreationCallback{
  
  private String _nameSpace, _localName, _name;
  private String _value;
  
  /**
   * Sets this attribute's name, that should be of the
   * form <b>namespace</b>:<b>localname</b>. The namespace is
   * optional. 
   * 
   * @param name this attribute's name.
   */
  public Attribute setName(String name){
    _name = name;
    int i;
    if((i = name.indexOf(':')) >= 0){
      _localName = name.substring(0, i);
      _nameSpace = name.substring(i+1);
    }
    else{
      _localName = name;
    }
    return this;
  }
  
  /**
   * @return the "namespace" part of this attribute's name.
   */
  public String getNameSpace(){
    return _nameSpace;
  }
  
  /**
   * @return the "local name" part of this attribute's name.
   */
  public String getLocalName(){
    return _localName;
  }  
  
  /**
   * @return the fully qualified name of this attribute. 
   */
  public String getName(){
    return _name;
  }
  
  /**
   * @param value some string value.
   * @return this instance.
   */
  public Attribute setValue(String value){
    _value = value;
    return this;
  }
  
  /**
   * @return this instance's value.
   */
  public String getValue(){
    return _value;
  }
  
  /**
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    if(_name == null || _value == null){
      return new NullObjectImpl();
    }
    return this;
  }
  
}
