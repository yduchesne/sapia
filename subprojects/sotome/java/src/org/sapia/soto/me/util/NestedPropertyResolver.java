package org.sapia.soto.me.util;

import javolution.util.FastMap;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class NestedPropertyResolver implements PropertyResolver {

  private PropertyResolver _parentResolver;
  
  private FastMap _properties;
  
  /**
   * Creates a new NestedPropertyResolver instance.
   * 
   * @param someProps The properties of this nested resolver.
   * @param aParent The parent property resolver of this nested resolver.
   */
  public NestedPropertyResolver(FastMap someProps, PropertyResolver aParent) {
    _parentResolver = aParent;
    _properties = new FastMap();
    _properties.putAll(someProps);
  }
  
  /**
   * Creates a new NestedPropertyResolver instance.
   * 
   * @param someProps The properties of this nested resolver.
   */
  public NestedPropertyResolver(FastMap someProps) {
    _properties = new FastMap();
    _properties.putAll(someProps);
  }
  
  /**
   * Creates a new NestedPropertyResolver instance.
   * 
   * @param aParent The parent property resolver of this nested resolver.
   */
  public NestedPropertyResolver(PropertyResolver aParent) {
    _parentResolver = aParent;
    _properties = new FastMap();
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.util.PropertyResolver#getProperty(java.lang.String)
   */
  public String getProperty(String aName) {
    if (aName == null) {
      throw new IllegalArgumentException("The property name passed is null");
    }
    
    String value = (String) _properties.get(aName);
    if (value == null && _parentResolver != null) {
      return _parentResolver.getProperty(aName);
    } else {
      return value;
    }
  }

}
