package org.sapia.soto.me.xml;

import org.sapia.soto.me.ConfigurationException;



/**
 * This interface is meant to be implemented by objects that are created by
 * the J2meConfix runtime, but are meant only for the sake of creating another object. This interface
 * can also be implemented if a validation needs to be performed before actually returning the
 * created object.<p>
 * 
 * This allows objects that do not obey the J2meConfix restrictions (adder and setter methods,
 * no-args constructor, etc) to still be created with Confix.
 * 
 * @author Jean-Cedric Desrochers
 */
public interface ObjectCreationCallback {
  
  /** Defines a static null object for empty creation callback. */
  public static final Object NULL_OBJECT = new Object();  
  
  /**
   * Called by the J2meConfix runtime when this instance has been created; allows
   * it to return another object at its place. The method can also be used
   * if validation needs to be performed before return this instance (or the
   * object that this instance creates).
   * 
   * If this method returns {@link ObjectCreationCallback#NULL_OBJECT}, the J2meConfix
   * runtime will assume that the created object was handled accordingly and that no
   * further handling (i.e. on the parent object) is necessary. 
   *
   * @return an <code>Object</code>
   * @throws ConfigurationException if an error occurs.
   */
  public Object onCreate() throws ConfigurationException;
}
