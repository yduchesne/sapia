package org.sapia.soto.util;

/**
 * An instance of this interface acts as a delegate for 
 * object creation.
 * 
 * @see Namespace#setDelegate(ObjectFactoryDelegate)
 * 
 * @author yduchesne
 *
 */
public interface ObjectFactoryDelegate {
  
  public Object newInstance(String className) throws Exception;

}
