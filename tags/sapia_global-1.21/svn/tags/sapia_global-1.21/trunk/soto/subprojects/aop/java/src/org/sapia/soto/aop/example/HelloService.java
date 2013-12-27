package org.sapia.soto.aop.example;

import org.sapia.soto.Service;

/**
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
public class HelloService implements Service {
  public void dispose() {
  }

  public void init() throws Exception {
  }

  public void start() throws Exception {
    helloWorld();
  }

  public void helloWorld() {
    System.out.println("Hello World");
  }
}
