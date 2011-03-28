package org.sapia.soto;

/**
 * Selects services that implement a given interface.
 * 
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class InstanceOfSelector implements ServiceSelector {

  private Class _instanceOf;

  public InstanceOfSelector(Class instanceOf) {
    _instanceOf = instanceOf;
  }

  /**
   * @see org.sapia.soto.ServiceSelector#accepts(org.sapia.soto.ServiceMetaData)
   */
  public boolean accepts(ServiceMetaData meta) {
    if(_instanceOf == null){
      return true;
    }
    else{
      return _instanceOf.isAssignableFrom(meta.getServiceClass());
    }
  }
}
