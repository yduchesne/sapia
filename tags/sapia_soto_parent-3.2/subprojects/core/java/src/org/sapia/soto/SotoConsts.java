package org.sapia.soto;

import org.sapia.soto.lifecycle.DefaultLifeCycleManager;

public interface SotoConsts {
  
  /**
   * Corresponds to the <code>soto.debug</code> property.
   */
  public static final String SOTO_DEBUG = "soto.debug";

  /**
   * Corresponds to the <code>soto.bootstrap</code> property.
   */
  public static final String SOTO_BOOTSTRAP = "soto.bootstrap";
  
  /**
   * This constant can be used as a key to bind a <code>SotoContainer</code>
   * 's environment (<code>Env</code>) into various application-related
   * contexts/maps. By convention, this constant should be used in such cases.
   * 
   * @see #toEnv()
   * @see Env
   */
  public static final String SOTO_ENV_KEY      = "SOTO_ENV";
  
  public static final String SOTO_INCLUDE_KEY = "org.sapia.soto";  
  
  /**
   * Identifies the {@link DefaultLifeCycleManager} in a {@link SotoContainer}
   * 
   * @see SotoContainer#registerLifeCycleManager(String, org.sapia.soto.lifecycle.LifeCycleManager)
   */
  public static final String SOTO_LIFE_CYCLE = "soto";

}
