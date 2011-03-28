package org.sapia.soto.state;

import java.util.Map;

/**
 * This interface should be implemented by <code>Context</code>
 * implementations that support the notion of "view" (as in MVC) and provide a
 * way to acquire the parameters that should be passed to the view.
 * 
 * @see org.sapia.soto.state.Context
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
public interface MVC {

  /**
   * This constant corresponds to the key under which the current object of the
   * <code>Context</code> is exported to the view. This key is a convention,
   * and it is intented to be used by all view technologies that will be plugged
   * into the framework.
   */
  public static String MODEL_KEY = "Model";

  /**
   * Returns the "view" parameters.
   * 
   * @return a <code>Map</code> of name/object bindinds that are intended for
   *         a "view".
   */
  public Map getViewParams();

}
