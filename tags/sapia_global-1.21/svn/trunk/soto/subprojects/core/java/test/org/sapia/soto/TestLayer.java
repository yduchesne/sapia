package org.sapia.soto;

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
public class TestLayer implements Layer {
  static boolean init;
  static boolean started;
  static boolean disposed;

  public void dispose() {
    disposed = true;
  }

  public void init(ServiceMetaData meta) throws Exception {
    init = true;
  }

  public void start(ServiceMetaData meta) throws Exception {
    started = true;
  }
}
