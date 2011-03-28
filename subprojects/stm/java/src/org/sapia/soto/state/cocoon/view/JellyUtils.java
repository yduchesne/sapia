package org.sapia.soto.state.cocoon.view;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.jelly.JellyContext;

/**
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
public class JellyUtils {

  /**
   * Copies the objects in the given parameter map to the given Jelly context.
   * This method spare existing variables the context.
   * 
   * @param ctx
   *          a <code>JellyContext</code>.
   * @param params
   *          a <code>Map</code> of parameters to bind into the context.
   */
  public static void copyParamsTo(JellyContext ctx, Map params) {
    Map.Entry entry;
    Iterator itr = params.entrySet().iterator();
    while(itr.hasNext()) {
      entry = (Map.Entry) itr.next();
      ctx.setVariable(entry.getKey().toString(), entry.getValue());
    }
  }

}
