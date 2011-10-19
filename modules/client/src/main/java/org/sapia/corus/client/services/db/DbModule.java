package org.sapia.corus.client.services.db;

import org.sapia.corus.client.Module;


/**
 * This module provides a persistency service to other modules.
 *
 * @author Yanick Duchesne

 */
public interface DbModule extends Module {
  public static final String ROLE = DbModule.class.getName();

  /**
   * Returns a <code>DbMap</code> instance for the given name.
   * It is the caller's responsability to ensure that the passed
   * in name does not duplicate another; callers should used qualified
   * names to retrieve <code>DbMap</code> instances.
   *
   * @param name the logical name of the desired <code>DbMap</code>.
   * @return a new <code>DbMap</code> if none exists for the given name,
   * or an already existing one if this applies.
   */
  public <K,V> DbMap<K, V> getDbMap(Class<K> keyType, Class<V> valueType, String name);
}
