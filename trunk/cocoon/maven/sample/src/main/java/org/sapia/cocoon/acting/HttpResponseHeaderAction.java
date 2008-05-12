package org.sapia.cocoon.acting;

import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.Action;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.sapia.cocoon.util.HttpCacheHeadersHelper;

/**
 * This action is inspired by a <a href="http://wiki.apache.org/cocoon/ControllingModCache">snippet on the Cocoon wiki</a>.
 * <p>
 * One can use this action to set the response headers that will trigger caching on the browser-side. This allows static
 * or seldomly changing content to be shielded from unnecessary requests from browsers. This greatly improves Cocoon scalability
 * (and user-perceived performance).
 * <p>
 * This action takes either one of the following sitemap configuration parameters:
 * 
 * <ul>
 *   <li><code>cache-validity-seconds</code>: the number of seconds that the browser should keep the resource for
 *   in its cache (defaults to 0).
 *   <li><code>cache-validity-minutes</code>: the number of minutes that the browser should keep the resource for
 *   in its cache (defaults to 0).   
 * </ul>
 * 
 * <b>Example Spring configuration:</b>
 * </p>
 * <pre>
 *    &lt;bean name="org.apache.cocoon.acting.Action/responseHeaders"
 *       class="org.sapia.cocoon.acting.HttpResponseHeaderAction" 
 *       scope="prototype" /&gt;   
 * </pre>  
 * <b>Example Sitemap configuration:</b>
 * </p>
 * <pre>
 *        &lt;map:act type="responseHeaders"&gt;            
 *          <map:parameter name="cache-validity-seconds" value="300" /&gt;
 *        </map:act&gt; 
 * </pre> 
 * 
 * @author yduchesne
 */
public class HttpResponseHeaderAction implements Action {
  
  public static final String PARAM_CACHE_VALIDITY_SECONDS = "cache-validity-seconds";
  public static final String PARAM_CACHE_VALIDITY_MINUTES = "cache-validity-minutes";
  
  public Map act(Redirector redirector, 
                 SourceResolver resolver, 
                 Map objectModel, 
                 String src, 
                 Parameters parameters) throws Exception {
    int nSec = parameters.getParameterAsInteger(PARAM_CACHE_VALIDITY_SECONDS,0);
    if(nSec == 0){
      nSec = parameters.getParameterAsInteger(PARAM_CACHE_VALIDITY_MINUTES,0) * 60;
    }

    HttpCacheHeadersHelper.setCacheHeaders(ObjectModelHelper.getResponse(objectModel),null,nSec);

    // don't execute what's inside this action, it's just here to set headers
    return null;
  }

}
