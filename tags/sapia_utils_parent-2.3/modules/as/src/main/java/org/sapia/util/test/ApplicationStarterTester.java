package org.sapia.util.test;

import org.sapia.util.ApplicationStarter;

/**
 *
 *
 * @author Jean-Cedric Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ApplicationStarterTester {
  /**
   *
   */
  public static void main(String[] args) {

    try {
      System.out.println("\n==================================================================================================");
      ApplicationStarter.main(new String[] { "-asdebug",
                            "org.sapia.util.test.TestApp2", "MainServer", "config/mainServer.xml", "config/mainServer.log" });
      Thread.sleep(1000);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      System.out.println("\n==================================================================================================");
      ApplicationStarter.main(new String[] { "-asdebug", "-ascp", "C:\\opt\\test\\lib\\;C:\\opt\\test\\lib\\jms.jar",
                            "com.newtrade.polaris.TestApp2", "MainServer", "config/mainServer.xml", "config/mainServer.log" });
      Thread.sleep(1000);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      System.out.println("\n==================================================================================================");
      ApplicationStarter.main(new String[] { "-asdebug",
                            "com.newtrade.polaris.TestApp", "MainServer", "config/mainServer.xml", "config/mainServer.log" });
    } catch (Exception e) {
      e.printStackTrace();
      try { Thread.sleep(1000); } catch (InterruptedException ie) {}
    }

    try {
      System.out.println("\n==================================================================================================");
      ApplicationStarter.main(new String[] { "-asdebug", "-ascp", "C:\\opt\\test\\lib\\;C:\\opt\\test\\lib\\jms.jar",
                            "com.newtrade.polaris.TestApp", "MainServer", "config/mainServer.xml", "config/mainServer.log" });
      Thread.sleep(1000);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      System.out.println("\n==================================================================================================");
      ApplicationStarter.main(new String[] { "-ashelp",
                            "org.sapia.util.test.TestApp2", "MainServer", "config/mainServer.xml", "config/mainServer.log" });
      Thread.sleep(1000);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
