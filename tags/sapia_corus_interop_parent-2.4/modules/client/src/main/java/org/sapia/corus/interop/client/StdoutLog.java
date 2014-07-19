package org.sapia.corus.interop.client;


/**
 * Implements the <code>Log</code> interface over stdout. The default
 * log level is INFO.
 *
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class StdoutLog implements Log {
  public static final int DEBUG   = 0;
  public static final int INFO    = 1;
  public static final int WARNING = 2;
  public static final int FATAL   = 3;
  public static final String LOG_NAME = "CORUS-INTEROP";
  
  private int _lvl = FATAL;

  public void debug(Object o) {
    if (isValid(DEBUG)) {
      System.out.println(LOG_NAME+ " [DEBUG] " + o);
      System.out.flush();
    }
  }

  public void info(Object o) {
    if (isValid(INFO)) {
      System.out.println(LOG_NAME+ " [INFO] " + o);
      System.out.flush();
    }
  }

  public void info(Object o, Throwable t) {
    if (isValid(INFO)) {
      System.out.println(LOG_NAME+ " [INFO] " + o);
      t.printStackTrace(System.out);
      System.out.flush();
    }
  }  

  public void warn(Object o, Throwable t) {
    if (isValid(WARNING)) {
      System.out.println(LOG_NAME+ " [WARNING] " + o);
      t.printStackTrace(System.out);
      System.out.flush();
    }
  }

  public void warn(Object o) {
    if (isValid(WARNING)) {
      System.out.println(LOG_NAME+ " [WARNING] " + o);
      System.out.flush();
    }
  }

  public void fatal(Object o, Throwable t) {
    if (isValid(FATAL)) {
      System.out.println(LOG_NAME+ " [FATAL] " + o);
      t.printStackTrace(System.out);
      System.out.flush();
    }
  }

  public void fatal(Object o) {
    if (isValid(FATAL)) {
      System.out.println(LOG_NAME+ " [FATAL] " + o);
      System.out.flush();
    }
  }
  
  @Override
  public boolean isDebugEnabled() {
    return isValid(DEBUG);
  }
  
  @Override
  public boolean isInfoEnabled() {
    return isValid(INFO);
  }
  
  @Override
  public boolean isWarnEnabled() {
    return isValid(WARNING);
  }

  public boolean isValid(int lvl) {
    return lvl >= _lvl;
  }

  /**
   * @param level a debug level
   *
   * @see #DEBUG
   * @see #INFO
   * @see #WARNING
   * @see #FATAL
   */
  public void setLevel(int level) {
    _lvl = level;
  }
}
