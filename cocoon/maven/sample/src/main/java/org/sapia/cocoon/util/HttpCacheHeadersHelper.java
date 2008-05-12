package org.sapia.cocoon.util;

import java.util.Date;

import org.apache.cocoon.environment.Response;

/**
 * Inspired by <a href="http://wiki.apache.org/cocoon/ControllingModCache">snippet on Cocoon wiki</a>.
 * 
 * @author yduchesne
 */
public class HttpCacheHeadersHelper {
  
  public static final String LAST_MOD_HEADER = "Last-Modified";
  public static final String EXPIRES_HEADER = "Expires";
  public static final String CACHE_CONTROL_HEADER = "Cache-Control";
  public static final String PRAGMA_HEADER = "Pragma";
  
  static int count = 0;
  
  /** set headers so that the response is cached for nSeconds
   *  @param lastModified if null, now - 2 seconds is used */
  public static void setCacheHeaders(Response resp, Date lastModified,int cacheForHowManySeconds) {

    long lastModTime = (lastModified == null ? System.currentTimeMillis() - 2000L : lastModified.getTime());
    long expires = System.currentTimeMillis() + (cacheForHowManySeconds * 1000L);
    
    resp.addDateHeader(LAST_MOD_HEADER,lastModTime);
    resp.addDateHeader(EXPIRES_HEADER,expires);
    resp.addHeader(CACHE_CONTROL_HEADER,"max-age="+ cacheForHowManySeconds + ", cache");
    resp.addHeader(PRAGMA_HEADER,"cache");
  }  

}
