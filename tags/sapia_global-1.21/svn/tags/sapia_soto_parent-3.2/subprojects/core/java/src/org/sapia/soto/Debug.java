package org.sapia.soto;

/**
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2003 <a
 *         href="http://www.sapia-oss.org">Sapia Open Source Software </a>. All
 *         Rights Reserved.</dd>
 *         </dt>
 *         <dt><b>License: </b>
 *         <dd>Read the license.txt file of the jar or visit the <a
 *         href="http://www.sapia-oss.org/license.html">license page </a> at the
 *         Sapia OSS web site</dd>
 *         </dt>
 *         </dl>
 */
public class Debug {
  public static final boolean DEBUG = (System.getProperty("soto.debug") != null) ? System
   .getProperty("soto.debug").equals(
   "true")
   : false;
  
  private Debug() {
  }
  
  public static final void debug(String msg) {
    if(DEBUG) {
      System.out.println("[SOTO-DEBUG] " + msg);
    }
  }
  
  public static final void debug(Throwable t){
    if(DEBUG) {
      System.out.println("[SOTO-DEBUG] Error: " + t.getMessage());
      t.printStackTrace(System.out);
    }
  }
  
  public static final void debug(Class origin, String msg) {
    if(DEBUG) {
      System.out.println("[SOTO-DEBUG: " + origin.getName() + "] " + msg);
    }
  }
  
  public static final void debug(Class origin, Throwable t){
    if(DEBUG) {
      System.out.println("[SOTO-DEBUG] : " + origin.getName() + "] Error: " + t.getMessage());
      t.printStackTrace(System.out);
    }
  }  
}
