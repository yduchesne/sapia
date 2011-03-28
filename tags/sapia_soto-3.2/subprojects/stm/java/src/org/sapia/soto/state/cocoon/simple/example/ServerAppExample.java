package org.sapia.soto.state.cocoon.simple.example;

import java.io.File;

import org.sapia.soto.SotoContainer;

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
public class ServerAppExample {

  public static void main(String[] args) {
    try {
      SotoContainer cont = new SotoContainer();
      cont.load(new File("etc/http/httpSample.xml"));
      cont.start();
      while(true) {
        Thread.sleep(100000);
      }
    } catch(Exception e) {
      e.printStackTrace();
    }

  }

}
