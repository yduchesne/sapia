package org.sapia.soto;

import java.io.File;
import java.sql.Timestamp;

/**
 * This class loads a Soto container as a stand-alone Java application. 
 * It provides a <code>main()</code> method that expects the path to the
 * Soto configuration file of the application to load.
 *
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class SotoMain {
  
  public static void main(String[] args) {
    if(args.length == 0) {
      log("Expecting <filename>[ <shutdown timeout millis>] as argument");
      return;
    }

    try {
      SotoContainer cont = new SotoContainer();
      
      if (args[0].indexOf(':') > -1) {
        cont.load(args[0], null);
      } else {
        cont.load(new File(args[0]), null);
      }
      cont.start();

      long timeoutMillis = 60000;
      if (args.length > 1) {
        try {
          timeoutMillis = Long.parseLong(args[1]);
        } catch (RuntimeException re) {
          log("Error parsing shutdown timeout value, using default value: " + re);
        }
      }
      
      Runtime.getRuntime().addShutdownHook(new ShutdownThread(cont, timeoutMillis));
      System.out.println();
      log("Soto application loaded & shutdown hook registered\n\t ** Type CTRL-C to stop ** ");

      while(true) {
        Thread.sleep(Long.MAX_VALUE);
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  private static void log(String aMessage) {
    StringBuffer buffer = new StringBuffer().
            append(new Timestamp(System.currentTimeMillis())).
            append(" [").append(Thread.currentThread().getName()).append("] ").
            append(aMessage);
    System.out.println(buffer.toString());
  }
  
  /**
   * Inner class that implements the shutdown procedure.
   * 
   * @author jcdesrochers
   */
  public static class ShutdownThread extends Thread {
    
    private SotoContainer _container;
    private long _shutdownTimeoutMillis;
    
    /**
     * Creates a new {@link ShutdownThread} instance.
     * 
     * @param aContainer
     * @param aTimeoutMillis
     */
    public ShutdownThread(SotoContainer aContainer, long aTimeoutMillis) {
      _container = aContainer;
      _shutdownTimeoutMillis = aTimeoutMillis;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    public void run() {
      try {
        log("Shutting down Soto application asynchronously with timeout of " + _shutdownTimeoutMillis + " millis...");
        
        Thread t = new Thread("SotoMain-ShutdownThread") {
          public void run() {
            try {
              log("Disposing soto container...");
              _container.dispose();
              
            } catch (Exception e) {
              log("*** ERROR occurred while disposing Soto container: " + e);
              
            } finally {
              log("Soto container closed");
            }
          }
        };
        t.setDaemon(true);
        t.start();
        
        t.join(_shutdownTimeoutMillis);
        if (t.isAlive()) {
          log("*** WARNING: shutdown thread still active after timeout period: interrupt thread and exit");
          t.interrupt();
        }
        
      } catch (Exception e) {
        log("*** ERROR occurred while shutting down Soto application: " + e);
        
      } finally {
        log("Shutdown completed - waiting for async tasks to complete...");
        try {
          Thread.sleep(3000);
        } catch (InterruptedException ie) {
        }
        log("...Bye.");
      }
    }
  }
}
